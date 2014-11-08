#!/bin/sh

apt-get -y install python-setuptools python-dev postgresql-server-dev-9.3
easy_install -UZ virtualenv
virtualenv /www/sentry/
source /www/sentry/bin/activate
easy_install -UZ sentry[postgres]
sentry init /etc/sentry.conf.py

sed -i -e 's/django.db.backends.sqlite3/django.db.backends.postgresql_psycopg2/' -e "s/'NAME': os.path.join(CONF_ROOT, 'sentry.db')/'NAME': 'sentry'/" -e "s/'USER': 'postgres'/'USER': 'sentry'/" -e "s/'PASSWORD': ''/'PASSWORD': 'pass'/" -e "s/'HOST': ''/'HOST': 'localhost'/" /etc/sentry.conf.py

apt-get -y install build-essential

wget http://download.redis.io/redis-stable.tar.gz
tar xvzf redis-stable.tar.gz
cd redis-stable
make
make install

echo "SENTRY_REDIS_OPTIONS = {" >> /etc/sentry.conf.py
echo "    'hosts': {" >> /etc/sentry.conf.py
echo "        0: {" >> /etc/sentry.conf.py
echo "            'host': '127.0.0.1'," >> /etc/sentry.conf.py
echo "            'port': 6379," >> /etc/sentry.conf.py
echo "        }" >> /etc/sentry.conf.py
echo "    }" >> /etc/sentry.conf.py
echo "}" >> /etc/sentry.conf.

sentry --config=/etc/sentry.conf.py upgrade