#!/bin/bash
DIR="."
# ## calculate datarate ##
# parameters:
# 1. datarate bash script file
# 2. title (headline in plot) and filename
# 3. y-axis maximum value (e.g. higher packet size required higher y-axis maximum value)
# 4. y-axis ticks
# 5. input file (.txt)
# 6. num packets
# 7. packet size (in bytes)
./datarate.sh "100 Pakete mit 100 bytes" 80 8 "$DIR/results_100p_100b" 100 100

./datarate.sh "1000 Pakete mit 100 bytes" 80 8 "$DIR/results_1000p_100b" 1000 100

./datarate.sh "10000 Pakete mit 100 bytes" 80 8 "$DIR/results_10000p_100b" 10000 100

mv *.png "$DIR"