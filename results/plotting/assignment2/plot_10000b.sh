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
./datarate.sh "100 Pakete mit 10000 bytes" 5000 500 "$DIR/results_100p_10000b" 100 10000

./datarate.sh "1000 Pakete mit 10000 bytes" 5000 500 "$DIR/results_1000p_10000b" 1000 10000

./datarate.sh "10000 Pakete mit 10000 bytes" 5000 500 "$DIR/results_10000p_10000b" 10000 10000

mv *.png "$DIR"