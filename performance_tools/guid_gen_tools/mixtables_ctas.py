#!/usr/bin/python
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

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
