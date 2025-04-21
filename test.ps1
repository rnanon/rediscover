$Host_Address = "localhost"
$Port = 9090
$GET_OPCODE = 0x01
$SET_OPCODE = 0x02

# Create the TCP client at the start
$global:client = $null
$global:stream = $null

function Connect-Server
{
    param (
        [string]$HostAddress,
        [int]$Port
    )

    try
    {
        $global:client = New-Object System.Net.Sockets.TcpClient
        Write-Host "Connecting to $HostAddress on port $Port..." -ForegroundColor Yellow
        $global:client.Connect($HostAddress, $Port)
        $global:stream = $global:client.GetStream()
        Write-Host "Connected successfully!" -ForegroundColor Green
        return $true
    }
    catch
    {
        Write-Host "Connection failed: $_" -ForegroundColor Red
        return $false
    }
}

function Disconnect-Server
{
    if ($global:stream)
    {
        $global:stream.Close()
    }
    if ($global:client)
    {
        $global:client.Close()
    }
    Write-Host "Disconnected from server." -ForegroundColor Yellow
}

function Send-ProtocolRequest
{
    param (
        [string]$Key,
        [byte]$Opcode,
        [string]$Value = ""  # Optional parameter for SET operation
    )

    if (-not $global:client -or -not $global:client.Connected)
    {
        Write-Host "Not connected to server. Reconnecting..." -ForegroundColor Yellow
        if (-not (Connect-Server -HostAddress $Host_Address -Port $Port))
        {
            return
        }
    }

    try
    {
        # Convert strings to byte arrays
        $keyBytes = [System.Text.Encoding]::UTF8.GetBytes($Key)
        $keyLength = $keyBytes.Length

        $valueBytes = [System.Text.Encoding]::UTF8.GetBytes($Value)
        $valueLength = $valueBytes.Length

        # Calculate message size based on operation
        $messageSize = 1 + 4 + $keyLength + 4
        if ($valueLength -gt 0)
        {
            $messageSize += $valueLength
        }

        $message = New-Object byte[] $messageSize

        # Set the OPCODE
        $message[0] = $Opcode

        # Set the key length (4 bytes, big endian)
        $message[1] = [byte](($keyLength -shr 24) -band 0xFF)
        $message[2] = [byte](($keyLength -shr 16) -band 0xFF)
        $message[3] = [byte](($keyLength -shr 8) -band 0xFF)
        $message[4] = [byte]($keyLength -band 0xFF)

        # Copy the key bytes
        [Array]::Copy($keyBytes, 0, $message, 5, $keyLength)

        # Set value length (4 bytes, big endian)
        $valueStartIndex = 5 + $keyLength
        $message[$valueStartIndex] = [byte](($valueLength -shr 24) -band 0xFF)
        $message[$valueStartIndex + 1] = [byte](($valueLength -shr 16) -band 0xFF)
        $message[$valueStartIndex + 2] = [byte](($valueLength -shr 8) -band 0xFF)
        $message[$valueStartIndex + 3] = [byte]($valueLength -band 0xFF)

        # Copy value bytes (if any)
        if ($valueLength -gt 0)
        {
            [Array]::Copy($valueBytes, 0, $message, $valueStartIndex + 4, $valueLength)
        }

        $operationType = if ($Opcode -eq $GET_OPCODE)
        {
            "GET"
        }
        else
        {
            "SET"
        }
        Write-Host "`nSending $operationType request with:" -ForegroundColor Cyan
        Write-Host "OPCODE: $Opcode" -ForegroundColor Cyan
        Write-Host "Key Length: $keyLength" -ForegroundColor Cyan
        Write-Host "Key: $Key" -ForegroundColor Cyan
        Write-Host "Value Length: $valueLength" -ForegroundColor Cyan
        if ($valueLength -gt 0)
        {
            Write-Host "Value: $Value" -ForegroundColor Cyan
        }

        $hexMessage = ($message | ForEach-Object { "{0:X2}" -f $_ }) -join " "
        Write-Host "Raw bytes being sent:" -ForegroundColor Green
        Write-Host $hexMessage

        # Send the message
        Write-Host "Sending message..."
        $global:stream.Write($message, 0, $message.Length)

        # Give server time to process
        Start-Sleep -Milliseconds 5000

        # Read response
        $response = New-Object byte[] 4096
        $bytesRead = $global:stream.Read($response, 0, $response.Length)

        Write-Host "Response (hex):" -ForegroundColor Green
        $hexResponse = ($response[0..($bytesRead - 1)] | ForEach-Object { "{0:X2}" -f $_ }) -join " "
        Write-Host $hexResponse

        if ($bytesRead -gt 5)
        {
            $responseValueLength = (($response[1] -shl 24) -bor ($response[2] -shl 16) -bor ($response[3] -shl 8) -bor $response[4])

            if ($bytesRead -ge (5 + $responseValueLength))
            {
                $responseValueBytes = $response[5..(5 + $responseValueLength - 1)]
                $responseValue = [System.Text.Encoding]::UTF8.GetString($responseValueBytes)
                Write-Host "Decoded value:" -ForegroundColor Green
                Write-Host $responseValue
            }
        }
    }
    catch
    {
        Write-Host "Error during request: $_" -ForegroundColor Red
        # Try to reconnect for next request
        Disconnect-Server
    }
}

# Main loop
try
{
    if (Connect-Server -HostAddress $Host_Address -Port $Port)
    {
        while ($true)
        {
            Write-Host "`n=== Protocol Client Menu ===" -ForegroundColor Magenta
            Write-Host "1. Send GET request" -ForegroundColor White
            Write-Host "2. Send SET request" -ForegroundColor White
            Write-Host "3. Exit" -ForegroundColor White
            $choice = Read-Host "Enter choice (1, 2, or 3)"

            switch ($choice)
            {
                "1" {
                    $key = Read-Host "Enter key to GET"
                    Send-ProtocolRequest -Key $key -Opcode $GET_OPCODE
                }
                "2" {
                    $key = Read-Host "Enter key to SET"
                    $value = Read-Host "Enter value"
                    Send-ProtocolRequest -Key $key -Opcode $SET_OPCODE -Value $value
                }
                "3" {
                    Write-Host "Exiting..." -ForegroundColor Yellow
                    break
                }
                default {
                    Write-Host "Invalid choice. Please try again." -ForegroundColor Red
                }
            }

            if ($choice -eq "3")
            {
                break
            }
        }
    }
}
finally
{
    # Ensure we disconnect when done
    Disconnect-Server
}