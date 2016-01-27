
#!/bin/bash

tabledesc="Table"
coldesc="Col"
comma=" , "
space=" "

createTable(){
	start=$1
	end=$2
	colstart=1
	colend=$3

	for (( table=$start; table<=$end; table++ )) ;
	do
		echo "creating table $table"
   		for (( col=$colstart; col<=$colend; col++ )) ;
        	do
                	rand=$(($RANDOM % 4))
                	case $rand in

                        	0) datatype="int" ;;
                        	1) datatype="string" ;;
                        	2) datatype="date" ;;
                        	3) datatype="float" ;;
                        	*) datatype="string"

                	esac


                	if [ $col == 1 ]
                 	then
                        	str=$tabledesc$table$coldesc$col$space$datatype
                	else
                        	str=$str$comma$tabledesc$table$coldesc$col$space$datatype
                	fi

        	done
		hive -e "create table $tabledesc$table ($str);"
		str=""
	done
}


read -p "Enter the number of tables to be generated" tableCount

read -p "Enter the percentage of small table (10 columns)" smallTableCount

read -p "Enter the percentage of medium table (50 columns)" mediumTableCount

# calculating number of small tables from the percentage
smallTableCount=`echo "$tableCount * ($smallTableCount / 100)" | bc -l`
mediumTableCount=`echo "$tableCount * ($mediumTableCount / 100)" | bc -l`

#rounding the variables to 0 precision (integer)
smallTableCount=`echo $smallTableCount | xargs printf "%.*f\n" 0`
mediumTableCount=`echo $mediumTableCount | xargs printf "%.*f\n" 0`
bigTableCount=$(($tableCount - ($smallTableCount + $mediumTableCount) ))


# createTable(Starting Table Number, Ending Table Number, Number of columns ) 
echo "Generating $smallTableCount small tables (10 columns) "	
createTable 1 $smallTableCount 10

echo "Generating $mediumTableCount medium tables (50 columns) "
createTable $(($smallTableCount + 1)) $(($smallTableCount + $mediumTableCount)) 50

echo "Generating $bigTableCount large tables (100 columns) "
createTable $(($smallTableCount + $mediumTableCount + 1)) $(($tableCount)) 100


hive -e "show tables;"

echo "Executing the hive query - ends"

