#!/usr/bin/python
import re;
import time;
import subprocess;
import commands;
import sys;

status, file_path = commands.getstatusoutput("echo $HIVE_HOME/logs/hive.log")
file=open(file_path);
content=file.readlines()

s_ms=0
m_ms=0
l_ms=0


small_s=1
small_e=int(sys.argv[1])

medium_s=small_e+1
medium_e=int(sys.argv[2])

large_s=medium_e+1
large_e=int(sys.argv[3])

#to find the tables which are not imported
table_hash=[0]*(large_e+1)

small_t_cnt=0
medium_t_cnt=0
large_t_cnt=0
start_time=""
success=1
end_time="end"

for line in content:
	start_match=re.match('([2][0-9][0-9][0-9]\-[0-2][0-9]\-[0-3][0-9] [0-2][0-9]\:[0-5][0-9]\:[0-5][0-9])\,([0-9]*)(.*)hook.HiveHook \(HiveHook\.java\:fireAndForget(.*)Entered Atlas hook for hook type POST_EXEC_HOOK operation CREATETABLE',line)
	end_match=re.match('([2][0-9][0-9][0-9]\-[0-2][0-9]\-[0-3][0-9] [0-2][0-9]\:[0-5][0-9]\:[0-5][0-9])\,([0-9]*)(.*)bridge\.HiveMetaStoreBridge \(HiveMetaStoreBridge\.java\:createOrUpdateTableInstance(.*)Importing objects from default.table_([0-9]*)',line)
	if start_match:
		if(not end_time):
			print "Incorrect logs"
			print "Import statement for the table is missing. But POST_EXEC_HOOK operation of CREATETABLE found"
			success=0
			break
		start_time=start_match.group(1)
		st_ms=start_match.group(2)
		stime=time.mktime(time.strptime(start_time, '%Y-%m-%d %H:%M:%S'));
		end_time=""
	if end_match:
		if(not start_time):
			print "Incorrect logs"
			print "Importing object log found before POST_EXEC_HOOK operation of CREATETABLE"
			success=0
			break
		end_time=end_match.group(1)
                end_ms=int(end_match.group(2))
                table_no=int(end_match.group(5))
		table_hash[table_no]=1
		etime=time.mktime(time.strptime(end_time, '%Y-%m-%d %H:%M:%S'));
		diff_ms=1000-int(st_ms)+int(end_ms)
                diff_s=etime-stime
                if(diff_ms>=1000):
                        diff_ms=diff_ms%1000
		if(diff_ms<1000):
			if(diff_s!=0):
				diff_s=diff_s-1
		t_ms=(diff_s)*1000+diff_ms
                if((table_no>=small_s)and(table_no<=small_e)):
                	s_ms=s_ms+t_ms
			small_t_cnt+=1
                elif((table_no>=medium_s)and(table_no<=medium_e)):
                        m_ms=m_ms+t_ms
			medium_t_cnt+=1
                elif((table_no>=large_s)and(table_no<=large_e)):
                        l_ms=l_ms+t_ms
			large_t_cnt+=1
		start_time=""

if (success==1):
	print "Total time for small tables(10 columns)",s_ms/1000," seconds for",small_t_cnt," tables. Average small table import time :",s_ms/(1000*small_t_cnt)
	print "Total time for medium tables(50 columns)",m_ms/1000," seconds for",medium_t_cnt," tables. Average medium table import time :",m_ms/(1000*medium_t_cnt)
	print "Total time for large tables(100 columns)",l_ms/1000," seconds for",large_t_cnt," tables. Average large table import time :",l_ms/(1000*large_t_cnt)
		
