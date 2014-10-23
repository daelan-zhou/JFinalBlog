#!/bin/sh
git_path="/home/ubuntu/git_pro/jnode"
war_path=$git_path"/build/ROOT.war"
pro_path="/home/ubuntu/www/jnode"
tom_bin="/home/ubuntu/mnt/tomcat_jnode/bin"
echo "goto..."$git_path
cd $git_path
echo "git...pull...begin..."
git pull
echo "git...pull...end..."
echo "build...war...begin..."
ant war
echo "build...war...end..."
unzip -o $war_path -d $pro_path
echo "unzip...war...end..."
cp "/home/ubuntu/script/config.txt" $pro_path"/WEB-INF/classes"
echo "tomcat...reload...begin..."
sh $tom_bin"/shutdown.sh"
kill -9 `aux|grep tomcat_jnode |grep -v grep |grep java |awk '{print $2}'`
sh $tom_bin"/startup.sh"
echo "tomcat...reload...end....."