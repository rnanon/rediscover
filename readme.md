# Custom Binary Protocol Specification

## Overview
This document describes the binary protocol used for communication between clients and the key-value store server.

### Request

[1-byte OPCODE][4-byte key length][key][4-byte value length][value]

- **OPCODE**: 1 byte indicating the operation to perform.
- **Key Length**: 4 bytes (unsigned integer) indicating the length of the key in bytes.
- **Key**: The key as a byte array (UTF-8 encoded).
- **Value Length**: 4 bytes (unsigned integer) indicating the length of the value in bytes.
- **Value**: The value as a byte array (UTF-8 encoded). Only used for `SET` operations.

### Response Format

[1-byte STATUS][4-byte value length][value]

- **STATUS**: 1 byte indicating the result of the operation.
- **Value Length**: 4 bytes (unsigned integer) indicating the length of the value in bytes.
- **Value**: The value as a byte array (UTF-8 encoded). Only used for `GET` operations.

---

## Opcodes
| Opcode | Operation | Description                          |
|--------|-----------|--------------------------------------|
| `0x01` | GET       | Retrieve the value for a given key.  |
| `0x02` | SET       | Store a value for a given key.       |
| `0x03` | DELETE    | Delete a key-value pair.             |

---

## Status Codes
| Status Code | Description                            |
|-------------|----------------------------------------|
| `0x00`      | Success                                |
| `0x01`      | Key not found                          |
| `0x02`      | Invalid request (e.g., Invalid Opcode) |
| `0x03`      | Internal server error                  |

---

## Examples

### SET Request
Store the value `Alice` for the key `user1`.

**Request**: [0x02][4][user1][5][Alice]

**Response**: [0x00][0][]

---

### GET Request
Retrieve the value for the key `user1`.

**Request**: [0x01][4][user1][0][]

**Response**: [0x00][5][Alice]

---

### DELETE Request
Delete the key-value pair for `user1`.

**Request**: [0x03][4][user1][0][]

**Response**: [0x00][0][]

---

### Error Handling

#### Invalid Opcode
If the client sends an invalid opcode (e.g., `0x04`), the server responds with: [0x02][0][] // Invalid request

#### Missing Key
If the client sends a `GET` request for a non-existent key, the server responds with: [0x01][0][] // Key not found