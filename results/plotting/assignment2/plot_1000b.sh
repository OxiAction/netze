#!/bin/bash
DIR="."
# ## calculate datarate ##
# parameters:
# 1. datarate bash script file
# 2. title (headline in plot) and filename
# 3. y-axis maximum value (e.g. higher packet size required higher y-axis maximum value)
# 4. y-axis ticks (a good value is 10% of y-axis maximum)
# 5. input file (.txt)
# 6. num packets
# 7. packet size (in bytes)
./datarate.sh "100 Pakete mit 1000 bytes" 1000 100 "$DIR/results_100p_1000b" 100 1000

./datarate.sh "1000 Pakete mit 1000 bytes" 1000 100 "$DIR/results_1000p_1000b" 1000 1000

./datarate.sh "10000 Pakete mit 1000 bytes" 1000 100 "$DIR/results_10000p_1000b" 10000 1000

mv *.png "$DIR"