#!/bin/bash

json="$1"

echo "JSON received: $json"

extract_json_value() {
  echo "$json" | sed -n "s/.*\"$1\"[ ]*:[ ]*\"\([^\"]*\)\".*/\1/p"
}

name=$(extract_json_value "name")
echo "Extracted value: $name"
