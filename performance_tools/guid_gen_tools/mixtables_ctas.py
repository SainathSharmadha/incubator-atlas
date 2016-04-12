#!/usr/bin/python
import re;
import time;
import subprocess;
import commands;
import sys;
import datetime;
from random import randint;

args=str(sys.argv)
total=int((sys.argv[1]))
s_end=int((sys.argv[2]))
m_end=int((sys.argv[3]))
l_end=int((sys.argv[4]))

f=open('table_guid_ctas.txt')
lines=f.readlines()
for num in range(1,total):
	if num%3 == 1:
		llim=0
		ulim=s_end-1
	elif num%3 ==2 :
		llim=s_end
		ulim=m_end-1
	elif num%3==0:
		llim=m_end
		ulim=l_end-1
	tableno=randint(llim,ulim)
	print tableno
	print lines[tableno]
	with open('mixed_table_guid_ctas.txt', 'a') as guid_file:
        	guid_file.write("%s" %(lines[tableno]));
