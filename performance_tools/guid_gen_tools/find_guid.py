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
