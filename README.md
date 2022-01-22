# mandelbrot
A simple Java application which produces a drawing of the Mandelbrot set.
You can save a snapshot (with different resolution up to 8k) to be used as a wallpaper if you fancy, and up to 10 points of interest to be recalled at a later time.
Use your mouse wheel to zoom in and out. Clicking on any place to change screen centering. Click C to switch between color models (indexed and direct). Double-click to get back to a zoom factor of 1.0. (Due to floating point limitations, zoom is limited to a factor of 275.)
Use your keyboard's arrow up/down to change max iterations' number; left and right arrow to use more or less threads. Or use the menues!
The more cores you have the faster it will be as calculation is multithreaded.
Used some optimizations that you can find here: https://en.wikipedia.org/wiki/Plotting_algorithms_for_the_Mandelbrot_set
This is just a quick hack I wrote to test my new machine.
