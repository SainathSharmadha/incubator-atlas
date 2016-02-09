#!/usr/bin/python
import re;
import time;
import subprocess;
import commands;
import sys;

args=str(sys.argv)
status, file_path = commands.getstatusoutput("echo $HIVE_HOME/logs/hive.log")
file=open(file_path);
content=file.readlines()

#small table milliseconds , medium table milliseconds ,large table milliseconds
s_ms=0
m_ms=0
l_ms=0


#table numbers for small medium and large tables 
small_s=1
small_e=int(sys.argv[1])

medium_s=small_e+1
medium_e=int(sys.argv[2])

large_s=medium_e+1
large_e=int(sys.argv[3])


for line in content:
	start_match=re.match('([2][0-9][0-9][0-9]\-[0-2][0-9]\-[0-3][0-9] [0-2][0-9]\:[0-5][0-9]\:[0-5][0-9])\,([0-9]*).*parse\.ParseDriver(.*)Parsing command\: create table Table_([0-9]*)(.*)',line);
	end_match=re.match('([2][0-9][0-9][0-9]\-[0-2][0-9]\-[0-3][0-9] [0-2][0-9]\:[0-5][0-9]\:[0-5][0-9])\,([0-9]*)(.*)HiveServer2\-Background\-Pool(.*) hive.log \(MetaStoreUtils\.java\:updateUnpartitionedTableStatsFast(.*)Updated size of table Table_([0-9]*) to (.*)',line);
	if start_match:
		start_time=start_match.group(1)
		stable_no=int(start_match.group(4))
		stime=time.mktime(time.strptime(start_time, '%Y-%m-%d %H:%M:%S'));
		st_ms=start_match.group(2)
	if end_match:
		end_time=end_match.group(1)
		end_ms=int(end_match.group(2))
		etable_no=int(end_match.group(6))
		etime=time.mktime(time.strptime(end_time, '%Y-%m-%d %H:%M:%S'));
		diff_ms=1000-int(st_ms)+int(end_ms)
		diff_s=etime-stime
		if(diff_ms>=1000):
                        diff_ms=diff_ms%1000
                elif(diff_ms<1000):
                        if(diff_s!=0):
                                diff_s=diff_s-1
		if(stable_no==etable_no):
			t_ms=(diff_s)*1000+diff_ms
			if((stable_no>=small_s)and(stable_no<=small_e)):
				s_ms=s_ms+t_ms
			elif((stable_no>=medium_s)and(stable_no<=medium_e)):
				m_ms=m_ms+t_ms
			elif((stable_no>=large_s)and(stable_no<=large_e)):
                        	l_ms=l_ms+t_ms
		else:
			assert(stable_no!=etable_no),"Incorrect log file"


print "Total time for small tables(10 columns)",s_ms/1000," seconds for",small_e-small_s+1," tables. Average small table creation time :",s_ms/(1000*(small_e-small_s+1))
print "Total time for medium tables(50 columns)",m_ms/1000," seconds for",medium_e-medium_s+1," tables. Average medium table creation time :",m_ms/(1000*(medium_e-medium_s+1))
print "Total time for large tables(100 columns)",l_ms/1000," seconds for",large_e-large_s+1," tables. Average large table creation time :",l_ms/(1000*(large_e-large_s+1))

