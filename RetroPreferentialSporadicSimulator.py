"""
Created on Thu Jul 13 12:46:54 2017
FRACTAL RABBIT Python Prototype
@author: Created by Dylan Molho working for the United States Government
"""

import matplotlib.pyplot as plt
from matplotlib import cm
import seaborn
import numpy as np
from scipy import stats
import os
from pandas import Series, DataFrame

#%%

## 
class Mobility_Models:
    """(U) FRACTAL RABBIT with three stochastic processes -
    Agoraphobic Point Process,
    Retro-Preferential Process, and
    Sporadic Reporting Process. """
    def __init__(self, pt_set = np.array([[0.0,0.0]]), visitpts = np.array([[0.0,0.0]]), 
                 sporadic_rep = np.array([[0.0,0.0]]), sojournHistory = np.array([[0.0,0.0]]),
                 app_clump = 2, hausdim = 1.25, psi=13.75, numplaces = 350, nsteps = 150, 
                 seasa = 4.17, seasb = 2.98, maxspeed = 1.0, aMotion = .55, cMotion= 10.0, sReports = 1.5, cReports= 50,
                 nReports= .9, BSojourn= 0.287, minSojourn = 1.0/(24.0*3600.0), cSojourn= 6.0*3600.0, corrupt= .025):
        """doc """
        self.pt_set = pt_set # generated collection of points in agoraphobic point process, begin with arbitrary set or just (0,0)
        self.trajectory = [i for i in range(len(pt_set))]
        self.visitpts = visitpts
        self.sporadic_rep = sporadic_rep
        self.sojournHistory = sojournHistory
        self.app_clump = app_clump # agoraphobic clump frequency parameter
        self.hausdim = hausdim # Hausdorff dimension
        self.psi = psi # parameter > 0
        self.numplaces = numplaces # number of points not = to (0,0); hence numplaces +1 is true number
        self.nsteps = nsteps # desired number of steps in retro-preferential process = Length(trajectory)-1
        self.maxspeed = maxspeed
        self.aMotion =aMotion # Pareto exponent
        self.cMotion =cMotion # multiple of the minimum
        self.sReports =sReports # Zipf exponent
        self.cReports =cReports # Zipf maximum
        self.nReports =nReports # self-declared arbitrary; Bernoulli rate at which a sojourn generates >= 1 report
        self.BSojourn =BSojourn # Pareto exponent
        self.minSojourn =minSojourn # default is 1 seconds in units/day
        self.cSojourn =cSojourn # default is 1/4 day as multiple of the minimum
        self.seasa = seasa # for daily seasonality adjusment
        self.seasb = seasb # for daily seasonality adjusment
        self.corrupt = corrupt # proportion of locations which will be replaced by a random location
        
    def _samplept_unitdisk(self):
        rad = np.sqrt(np.random.uniform())
        zeta = np.random.uniform(high= 2*np.pi)
        # Random point in unit disk
        return np.array([rad*np.cos(zeta), rad*np.sin(zeta)])
        
    
    def ag_pointp(self):
        n = self.numplaces
        r = self.hausdim
        theta = self.app_clump
        init_count = len(self.pt_set)
        for j in range(n):
            newClumpProbability = theta/(theta + (j-1) + init_count)
            # Bernoulli Trial 
            startNewClump = (np.random.rand() < newClumpProbability)
            if startNewClump:  
                child = self._samplept_unitdisk()
                self.pt_set = np.append(self.pt_set, [child], axis=0)
            else:
                # Sample point in existin clump
                accept = True
                while accept:
                    parent = self.pt_set[np.random.choice(range(self.pt_set.shape[0]))]
                    radius = (j+init_count)**(-r)
                    child = parent + radius*self._samplept_unitdisk()
                    neighborCount = np.array([self.pt_set[i] for i in range(self.pt_set.shape[0]) if np.linalg.norm(child - self.pt_set[i]) <= radius]).shape[0]
                    accept = (np.random.rand() < 1/(neighborCount))
                self.pt_set = np.append(self.pt_set, [child], axis=0)
        return self.pt_set

    
    
    def retro_pref(self):
        n = self.pt_set.shape[0]
        
        # interpoint-distance matrix:
        idm = np.array([[1/np.dot((self.pt_set[i] - self.pt_set[j]), (self.pt_set[i] - self.pt_set[j])) if np.dot((self.pt_set[i] - self.pt_set[j]), (self.pt_set[i] - self.pt_set[j])) != 0 else 0 for j in range(n)] for i in range(n)])
        # Initialize
        self.trajectory = [0] # list of indices of locations visited: start at 0 (first point)
        self.visitCounts = np.zeros(n)
        self.visitCounts[1] =1 
        while len(self.trajectory) <= self.nsteps:
            place = self.trajectory[-1] # present location
            past = set(self.trajectory) # set of locations visited
            f = self.psi/(len(past) -1 + self.psi) #Probability of an outward step -- gives size of 'past' as approximately sqrt[time] 
            outwardStep = (np.random.rand() < f)
            # when revisitng previous place, weight by (# previous visits)/(squared distance from place)
            if outwardStep:
                transitionVector = np.array([0 if j in past else idm[place,j] for j in range(n)])
            else:
                transitionVector = np.array([self.visitCounts[j]*idm[place,j] if j in past else 0 for j in range(n)])
            ptVector = transitionVector/transitionVector.sum()
            # Random transition 
            place = np.where(np.random.multinomial(1, ptVector) == 1)[0][0]
            self.trajectory.append(place)
            self.visitCounts[place] += 1
        self.trajectory = np.array(self.trajectory)
        self.visitpts = self.pt_set[[self.trajectory]]
        return self.visitpts
        
    # attempt to get good plot of agoraphobic point process and retro-preferential behavior on that set, needs more work.
    def plotpts(self):
        fig = plt.figure()
        ax = fig.add_subplot(111)
        ax.scatter(self.pt_set[:,:1],self.pt_set[:,1:], s=1, c='black')
#        ax.scatter(self.pt_set[:,:1],self.pt_set[:,1:], s=1, cmap=cm.inferno)


    # Beta inverse CDF during day -- see global parameter set
    def season_adjust(self, t):
        return np.floor(t)+ stats.beta.ppf(t -np.floor(t), self.seasa, self.seasb)

    # helper function 
    def _flatten(self, item):
        for i in item:
            if isinstance(i, list):
                yield from self._flatten(i)
            else:
                yield i

    # requires existing pt_set and visitpts
    def sporadic_rep(self):
        n = len(self.visitpts)
        # interpoint-distance vector of trajectory points 
        idv = np.array([np.sqrt(np.dot((self.visitpts[i] - self.visitpts[i+1]), (self.visitpts[i] - self.visitpts[i+1]))) for i in range(n-1)])
        gamma = 1 - (1/self.cMotion)**self.aMotion
        Z = np.array([(1.0/self.maxspeed)*idv[j]/((1-gamma*np.random.rand())**(1/self.aMotion)) for j in range(n-1)])
        atLeastOneReport = np.random.binomial(1, self.nReports, size = n)
        # scipy nor numpy come with a bounded/truncated zipf, so we have to make one:
        x = np.arange(1, (self.cReports))
        weights =  x**(-self.sReports)
        weights /= weights.sum()
        bounded_zipf = stats.rv_discrete(name = 'bounded_zipf', values = (x,weights))
        self.uncensoredReportCounts = bounded_zipf.rvs(size = n) 
        self.reportCounts = atLeastOneReport*self.uncensoredReportCounts 
        kappa = 1 -(1/self.cSojourn)**self.BSojourn
        self.fineIntervals = [[self.minSojourn/((1-kappa * np.random.rand())**(1/self.BSojourn)) for i in range(self.reportCounts[j]+1)] for j in range(n)]
        self.corruptedTrajectory = np.array([np.random.randint(1, high = len(self.pt_set)) if np.random.rand()<self.corrupt else self.trajectory[j] for j in range(n)])
        self.corruptedR_P = self.pt_set[[self.corruptedTrajectory]]
        
        self.fineIntervals = np.array([np.array(list) for list in self.fineIntervals])
        Y = np.array([self.fineIntervals[i].sum() for i in range(len(self.fineIntervals))])
        Ycumulative = Y.cumsum()
        Zcumulative = Z.cumsum()
        sojournStarts = [0.0]
        sojournStarts.extend(np.delete(Ycumulative, -1)+Zcumulative)
        sporadicReports = [[self.season_adjust(sojournStarts[j] + self.fineIntervals[j][k]) for k in range(self.reportCounts[j])] 
                            if self.reportCounts[j] >= 1 else self.corruptedTrajectory[j].tolist() for j in range(n)]
        
        self.sporadicReports = np.array(list(self._flatten(sporadicReports)))
        
        sojournEnds = Y[0]
        sojournEnds = np.append(sojournEnds, (np.delete(Ycumulative, 1)+Zcumulative))
        #list of 3-ples of form (placeID, time-entered, time-departed) 
        self.sojournHistory = np.array([self.trajectory, self.season_adjust(sojournStarts), self.season_adjust(sojournEnds)]).transpose()
        return self.sporadicReports, self.sojournHistory                            


#%%
#path = 'C:\\Users\\djmolho\\AppData\\Local\\DYLAN Actual Files\\Retro Preferential\\darling_files\\RP test'
#file1 = "output.csv"
#if os.path.exists(file1):
#    Tdata = pd.read_csv(file1) 
#os.path.exists(file1) 
#
#pts = np.array([[Tdata.Latitude[i], Tdata.Longitude[i]] for i in range(len(Tdata))])
#
#test = Mobility_Models(pt_set = pts, numplaces = 100)








