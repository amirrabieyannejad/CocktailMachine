# Supported services

## General

UUID of the Cocktail Maschine: 0f7742d4-ea2d-43c1-9b98-bb4186be905d

## Communication Services

A communication service has two characteristics: 

1. a write characteristicone for sending a message (encoded as JSON) 
2. a read+notify characteristic to receive a respone. 

The protocol is always:

1. Register for a notification on the response characteristic.
2. Send the message by writing the JSON message to the message characteristic.
3. Read the response from the response characteristic as soon as the notification arrives.

Typically, a command only returns potential error codes as a response.

### Commands
- UUID:     dad995d1-f228-38ec-8b0f-593953973406
- message:  eb61e31a-f00b-335f-ad14-d654aac8353d
- response: 06dc28ef-79a4-3245-85ce-a6921e35529d

## Status Services

A status service has a number of read+notify characterists that contain the actual value, typically in the form of a JSON map.

### Pumps
- UUID: 1a9a598a-17ce-3fcd-be03-40a48587d04e

All the available pumps, the liquid they contain and its volume.

### Operation
- UUID: addf5391-2030-3cf0-a64f-31d5156d7f00

The current operation of the Cocktail Maschine.

### Recipes
- UUID: 8f0aec28-5985-335e-baa2-8e03ce08b513

All known cocktails.

### Drink
- UUID: aa1c770d-5998-3d0e-b8fa-4e9421e2e6d2

The content of the current drink being mixed, if any.
