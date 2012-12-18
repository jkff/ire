#!/bin/sh

sphinx-build -b html -D math-output=mathjax -a -E . ./_build
