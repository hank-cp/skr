#!/bin/sh

BASEDIR=$(dirname "$0")
echo "$BASEDIR"
pip3 install requests
pip3 install jsonpath
python3 -m unittest discover requests -s "$BASEDIR" -p "test_*.py"