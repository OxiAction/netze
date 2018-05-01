#!/bin/bash
gnuplot -persist <<-EOFMarker
    set title "$1" font ", 30"
    set ytics "$3" font ", 30" offset -1,0
    set xtics font ", 30" offset -1,-1
    set grid
    set xlabel "Durchlauf" font ", 30" offset 0,-3
    set ylabel "Datenrate in Mbit/s" font ", 30" offset -6,0
    set xrange [-1:51]
    set yrange [0:"$2"]
    set lmargin 20
    set bmargin 10
    set style fill transparent solid 0.35 noborder
    set style circle radius 0.35
    set terminal png truecolor enhanced size 1920,1080
    set output "$1.png"
    plot "$4.txt" using (\$0+1):(("$5"-\$2)*"$6"*8/(\$1/1000)/1024/1024) with circles lc rgb "red" t "Java nach Java"
EOFMarker