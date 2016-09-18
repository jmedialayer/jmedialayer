# jmedialayer

A Java Library similar to SDL that works with [JTransc](http://blog.jtransc.com/) to build native applications that work everywhere,
including browser and homebrew platforms like psvita and 3ds.

The idea is to create layers. An initial layer will provide functionality to handle pixel by pixel drawing and basic input. 
An posterior layer will provide accelerated 2d graphics.
And a final layer will provide 3d accelerated graphics with shader support.
