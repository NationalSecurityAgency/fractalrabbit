<p align="center">
  <a href="URL">
    <img src="https://github.com/NationalSecurityAgency/fractalrabbit/blob/master/resources/Rabbit-CreativeCommonsImage..jpg" alt="" width=370 height=247>
  </a>

  <h3 align="center">FRACTALRABBIT</h3>
<p>
In modelling a sequence of adaptive choices by an intelligent agent (e.g. places visited, web sites browsed), memory-less random walks are unsuitable, because of the formation of agent habits and preferences. 
 </p>

<p>
 Often these choices are only partially observed, and report times are sporadic and bursty, in contrast to regular or exponentially spaced times in classical models. 
</p>

<p>
The FRACTALRABBIT stochastic mobility simulator creates realistic synthetic sporadic waypoint data sets. It consist of three tiers, each based on new stochastic models: </p>

  <p align="center">	
	 (1) An Agoraphobic Point Process generates a set V of space points, whose limit is a random fractal, representing sites that could be visited. </p>

  <p align="center">	(2) A Retro-preferential Process generates a trajectory X through V , with strategic homing and self-reinforcing site ﬁdelity as observed in human/animal behavior. </p>

  <p align="center">	 (3) A Sporadic Reporting Process models time points T at which the trajectory X is observed, with bursts of reports and heavy tailed inter-event times.</p>
  </p>
</p>
<p>
 FRACTALRABBIT can be used to test algorithms applicable to sporadic waypoint data, such as (1) co-travel mining, (2) anomaly detection, and (3) extraction of maximal self-consistent subsets of corrupted data.
<p>
<p>
Reference: R. W. R. Darling, "Retro-preferential Stochastic Mobility Models on Random Fractals Under Sporadic Observations", 
<a href = "https://www.researchgate.net/publication/340741639_Retro-preferential_Stochastic_Mobility_Models_on_Random_Fractals_Under_Sporadic_Observations">DOI: 10.13140/RG.2.2.15267.40489</a>, 2018
<p>

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
	java -jar fractalrabbit.jar parameters.csv outputfilename.csv</p>
<p>	
An example of the parameters.csv file is provided in the resources folder.
Change it to suit your modelling needs. 
It permits multiple travellers to follow the same trajectory asynchronously.
</p>	

## Bugs and feature requests
- Have a bug or a feature request? Contact Github user bbux-atg

## Documentation
- See <a href="https://github.com/NationalSecurityAgency/fractalrabbit/wiki">Wiki</a>. 

## Contributing
- New implementations of the three underlying models described in the technical report are welcome.

## Creators

**R. W. R. Darling**
<a href=https://sites.google.com/view/probabilist-us/home>bio</a>
Github: probabilist-us

## Copyright and license

Apache License 2.0
