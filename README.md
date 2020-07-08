<p align="center">
  <a href="URL">
    <img src="https://github.com/NationalSecurityAgency/fractalrabbit/blob/master/RabbitProfile.jpg.jpg" alt="" width=134 height=144>
  </a>

  <h3 align="center">FRACTALRABBIT</h3>
<p>
The FRACTALRABBIT stochastic mobility simulator creates realistic synthetic sporadic waypoint data sets. It consist of three tiers, each based on new stochastic models: </p>

  <p align="center">	
	 (1) An Agoraphobic Point Process generates a set V of space points, whose limit is a random fractal, representing sites that could be visited. </p>

  <p align="center">	(2) A Retro-preferential Process generates a trajectory X through V , with strategic homing and self-reinforcing site ﬁdelity as observed in human/animal behavior. </p>

  <p align="center">	 (3) A Sporadic Reporting Process models time points T at which the trajectory X is observed, with bursts of reports and heavy tailed inter-event times.</p>
  </p>
</p>

<br>

## Table of contents

- [Status](#status)
- [Bugs and feature requests](#bugs-and-feature-requests)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [Community](#community)
- [Versioning](#versioning)
- [Creators](#creators)
- [Copyright and license](#copyright-and-license)

## Status
Java version runs from the command line:
<p>
	java -jar fractalrabbit.jar parameters.csv</p>
<p>	
An example of the parameters.csv file is given below. 
Change it to suit your modelling needs. 
It permits multiple travellers to follow the same trajectory asynchronously.
</p>	
Two historical versions are available: 
<p>
<a href="https://github.com/NationalSecurityAgency/fractalrabbit/blob/master/RetroPreferentialSporadicSimulator.nb">Mathematica Notebook, for independent trajectories</a>, visible in 
<a href="https://github.com/NationalSecurityAgency/fractalrabbit/blob/master/RetroPreferentialSporadicSimulatorCopy.pdf"> Text version </a></p>	
<p>
<a href="https://github.com/NationalSecurityAgency/fractalrabbit/blob/master/RetroPreferentialSporadicSimulator.py"> Python Version, for independent trajectories </a>
</p>


## Bugs and feature requests
- Have a bug or a feature request? Contact Github user bbux-atg

## Documentation
- See <a href="https://github.com/NationalSecurityAgency/fractalrabbit/wiki">Wiki</a>. 

## Contributing
- New implementations of the three underlying models described in the technical report are welcome.

## Community
- TBA

## Versioning
- 

## Creators

**R. W. R. Darling**
http://probabilist.us
Github: probabilist-us

## Copyright and license

Apache License 2.0
