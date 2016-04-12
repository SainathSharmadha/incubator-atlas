#!/bin/bash
cat logs/application | grep -E "(Read message|Sending message for topic ATLAS_ENTITIES.*col_1@.*ENTITY_UPDATE)" > important-logs.txt
