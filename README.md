# Scientific computation

In this module, we simulated fluids, simulated a CT scanner with an FFT and much more.

For the full report please read ![cw_up938652.pdf](cw_up938652.pdf)

# Highlights

## Fluid simulation

### Wing simulations

Here we simulated hydrofoils in high Reynolds fluids and calculated lift values from the relive forces over the foil. This came to the strange conclusion that the wing produces more lift with a negative AOA.

![Wing at 10deg AOA](Week8/Images/Air10deg.png)
![Wing at 10deg AOA](Week8/Images/AirNeg10Deg.png)


### Gas models

The FHP and HHP models use cellular automata. FHP uses a hex grid and more random movement, producing better diffusion and more accurate simulations
![FHP Model](Week5/images/FHP150q.png)

HHP uses a square grid. This produces voids in the gas due to the lack of random motion

![HHP Model](Week5/images/HHP150q.png)

### CT Scanner simulation

I took a low contrast Shepp–Logan phantom and simulated a CT scanner to produce a Sinogram. This was then filtered using various filters to clear up the final back propagation image.

![CT scan image](Week3/images/RampFilter.png)
