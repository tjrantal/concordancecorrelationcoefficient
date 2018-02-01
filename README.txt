Implementation of concordance correlation coefficient as described in wikipedia
	https://en.wikipedia.org/wiki/Concordance_correlation_coefficient
	
I have used this to synchronise two signals with non-similar amplitudes. Cross-correlation performs poorly when the signal amplitudes are not similar, and I've found concordance correlation to perform much better on such occasions.

Written by Timo Rantalainen 2013 - 2018 tjrantal at gmail dot com. Contributions taken from the pache commongs math 3.0 git clone http://git-wip-us.apache.org/repos/asf/commons-math.git, which is licensed under the Apache 2.0 license, which also applies to this project.

Look into build.xml for building with ant. Usage sample from Matlab in test.m
