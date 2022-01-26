# mandelbrot
A simple Java application that produces a drawing of the Mandelbrot set. The more cores your machine has, the faster it will be as calculation is multithreaded.<br/>
Use your mouse wheel to zoom in and out. Zooming is relative to the pointer's coordinates. Due to floating point limitations, zoom is limited to a factor of 275. Click on any place to change centering. Double-click to get back to a zoom factor of 1.0.<br/>
Coloring is done via an indexed palette (Color mode: Indexed), a palette that tries to produce a smooth color transition (Color mode: Mode 1) or a palette that emphasizes difference between iterations (Color mode: Mode 2). Some points of interest are best viewed with Mode 1, others via Mode 2 (e.g. the standard view of the Mandelbrot set). Try them!<br/>
If there's a particular point you are interested in, you can save it as a <i>point of interest</i> and recall it at a later time. To delete a point of interest you're not interested in anymore, select it first and then delete it from the menu. The list of points of interest is saved in the user's home directory in a folder called .com.threeamigos.mandelbrot. You can manually edit this file. (e.g. to reorder the entries or to delete some of them).<br/>
You can save a snapshot (with different resolution up to 8k) to be used as a wallpaper if you fancy.<br/>
<br/>
Keyboard shortcuts:<br/>
C - cycles through the color models.<br/>
D - deletes current point of interest (if any is selected).<br/>
P - shows or hides current point of interest's name (if any is selected).<br/>
H - hides or shows keyboard shortcuts in the main window.<br/>
I - hides or shows information about the region you're in.<br/>
S - saves a snapshot.<br/>
Arrow up/down: doubles/halves max iterations.<br/>
Arrow left/right: uses more or less threads.<br/>
Numeric keys 1 to 0 recall points of interest 1 to 10 (other points have to be recalled via the Points of Interest menu).<br/>
<br/>
The calculation part uses some optimizations that you can find here: https://en.wikipedia.org/wiki/Plotting_algorithms_for_the_Mandelbrot_set
