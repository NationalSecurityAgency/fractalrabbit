USAGE: java -jar fractalrabbit.jar parameters.csv outfile
REMARKS: Version of java should be 17 or later. Example of parameters.csv is in resources folder. Name of .jar file may vary.

In modelling a sequence of adaptive choices by an intelligent agent (e.g. places visited, web sites browsed), memory-less random walks are unsuitable, because of the formation of agent habits and preferences. 
 
 Often these choices are only partially observed, and report times are sporadic and bursty, in contrast to regular or exponentially spaced times in classical models. 
 
 The FRACTALRABBIT stochastic mobility simu lator creates realistic synthetic sporadic waypoint data sets. It consist of three tiers, each based on new stochastic models: 
 
 1. An Agoraphobic Point Process generates a set V of points in R^d , whose limit is a random fractal, representing sites that could be visited. 
 
 2. A Retro-preferential Process generates a trajectory X through V , with strategic homing and self-reinforcing site fidelity as observed in human/animal behavior. 
 
 3. A Sporadic Reporting Process models time points T at which the trajectory X is observed, with bursts of reports and heavy tailed inter-event times. 
 
 FRACTALRABBIT is being used to test algorithms applicable to sporadic waypoint data, such as (1) co-travel mining, (2) anomaly detection, and (3) extraction of maximal self-consistent subsets of corrupted data.

Reference: R. W. R. Darling, "Retro-preferential Stochastic Mobility Models on Random Fractals Under Sporadic Observations", 
<a href = "https://www.researchgate.net/publication/340741639_Retro-preferential_Stochastic_Mobility_Models_on_Random_Fractals_Under_Sporadic_Observations">DOI: 10.13140/RG.2.2.15267.40489</a>, 2018
