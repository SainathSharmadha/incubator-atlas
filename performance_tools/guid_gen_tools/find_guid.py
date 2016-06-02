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


status, file_path = commands.getstatusoutput("echo guids_logs.txt")
status,cmd=commands.getstatusoutput("rm table_guid")
status,cmd=commands.getstatusoutput("touch table_guid.txt")
file=open(file_path);
content=file.readlines()


for line in content:

	line1=line[0:550]
	match=re.match('(.*)Sending message for topic ATLAS_ENTITIES(.*)\"id\"\:\"(.*)\"\,\"version\"(.*)name\"\:\"(.*)\"\,\"createTime(.*)',line1)
	if match:
		print line1
		with open('table_guid.txt', 'a') as guid_file:
			guid_file.write("%s,%s\n" %(match.group(5),match.group(3)));
	else:
		print "no match"
