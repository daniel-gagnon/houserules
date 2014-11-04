# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

$rethink = <<SCRIPT
source /etc/lsb-release && echo "deb http://download.rethinkdb.com/apt $DISTRIB_CODENAME main" | sudo tee /etc/apt/sources.list.d/rethinkdb.list
wget -qO- http://download.rethinkdb.com/apt/pubkey.gpg | sudo apt-key add -
apt-get update
apt-get -y install rethinkdb

cp /etc/rethinkdb/default.conf.sample /etc/rethinkdb/instances.d/instance1.conf
sed -i /etc/rethinkdb/instances.d/instance1.conf -e 's/# bind=127.0.0.1/bind=all/'
/etc/init.d/rethinkdb restart
SCRIPT

$lein = <<SCRIPT
mkdir /home/vagrant/bin
wget -qO /home/vagrant/bin/lein https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
chmod a+x /home/vagrant/bin/lein
echo 'export PATH="/home/vagrant/bin:$PATH" >> /home/vagrant/.bashrc'
SCRIPT

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "ubuntu/trusty64"
  config.vm.network "forwarded_port", guest: 8080, host: 8080
  config.vm.network "forwarded_port", guest: 3000, host: 3000
  config.vm.provision "shell", inline: $rethink
  config.vm.provision "shell", inline: $lein
end
