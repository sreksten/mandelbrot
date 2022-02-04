package com.threeamigos.mandelbrot.interfaces.ui;

import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import javax.swing.event.MouseInputListener;

public interface InputConsumer extends MouseWheelListener, MouseInputListener, MouseMotionListener, KeyListener {

}
