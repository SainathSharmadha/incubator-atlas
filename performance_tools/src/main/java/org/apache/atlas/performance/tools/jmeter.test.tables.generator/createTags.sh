#!/bin/bash

read -p "enter no of tables" ntable
read -p "enter the percentage of tables to contain tags" tabletag
read -p "enter the number of tags" ntag

ntables=`echo "$ntable * ( $tabletag /100 )" | bc -l`
echo $ntables
ntables=`echo $ntables | xargs printf "%.*f\n" 0`

echo $ntables

i=0
tagfile="tagtables.txt"
rm $tagfile
touch $tagfile
rm tags-attribs.txt
touch tags-attribs.txt

end=$ntables
inc=`echo "$ntables/$ntag"|bc -l`
inc=`echo $inc | xargs printf "%.*f\n" 0`
if(inc == 0) then
	inc=$ntables
fi
tag_start=1
tag_end=ntag
start=0
attribute=""
value=""
tagname=""
cluster="erie-perf-test-cluster"
tagcount=1

table_end=0
for ((tag=tag_start;tag<=tag_end;tag++));
do
table_start=`echo "$table_end+1"|bc -l`
table_end=`echo "$table_end+$inc"|bc -l`
printf $tag"\n"
tagname="tag"$tag
attribute=$tagname"_attrib"
echo $tagname,$attribute>>tags-attribs.txt

for (( table=$table_start; table<=$table_end;table++)) ;
        do
                tablename=default.table_$table@$cluster
                value=$attribute"_val_$table"
                tableinfo=`cat table_guid.txt|grep -i "$tablename"`
                echo $tableinfo,$tagname,$attribute,$value >> $tagfile
                printf $table"\t"
        done
        printf "\n"
done


