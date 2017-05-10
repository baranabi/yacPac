#!/bin/bash
for n in {1..100}; do
  dd if=/dev/urandom of=$( printf %03d "$n" ).bin bs=1 count=$(( RANDOM + 1024))
done
