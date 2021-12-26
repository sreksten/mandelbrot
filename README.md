# mandelbrot
A simple Java application which produces a drawing of the Mandelbrot set.
Use your mouse wheel to zoom in and out. Clicking on any place to change screen centering. Click C to switch between color models (indexed and direct). Double-click to get back to a zoom factor of 1.0.
Due to floating point limitations, zoom is limited to a factor of 275).
The more cores you have the faster it will be as calculation is multithreaded.
Used some optimizations that you can find here: https://en.wikipedia.org/wiki/Plotting_algorithms_for_the_Mandelbrot_set
This is just a quick hack I wrote to test my new machine.
