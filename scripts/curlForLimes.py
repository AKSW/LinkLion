#!/usr/bin/env python

# curlForLimes data
#
# The following curl statement is created for every */positive.nt file in a 
# directory (example):
# curl -i 
# -F "file=@/home/markus/Mapping/limesSpecsAll/bnb-dbpedia-novelists/positive.nt" 
# -F "new-source-name=BNB" 
# -F "new-source-uri=http://bnb.data.bl.uk" 
# -F "new-target-name=DBpedia"
# -F "new-target-uri=http://dbpedia.org" 
# -F "new-framework-name=LIMES" 
# -F "new-framework-version=1.2" 
# -F "new-framework-url=http://limes.aksw.org" 
# -F "new-algorithm-name=EAGLE" 
# -F "new-algorithm-url=http://www.foo.com"
# http://www.linklion.org/portal/rest/file/upload
#
# Usage:
# This script takes all positive.nt files *in* all directories. Source
# and target information is extracted from the first line in the positive.nt
# file (care for comments) -
# if your file format differs, you need to change the importer.
# Some information is static, change it here.
# If you process large amounts of files, test it first with 1 or 2 files.

import inspect, os, glob, shlex, subprocess, time

def set_st_uris(name):
    with open(name) as f:
        fl = f.readline()

    fl_split = fl.split()
    raw_source = fl_split[0].split('/')
    raw_target = fl_split[2].split('/')

    dict['new-source-name'] = raw_source[2]
    dict['new-source-urispace'] = raw_source[0][1:] + '//' + raw_source[2]
    dict['new-target-name'] = raw_target[2]
    dict['new-target-urispace'] = raw_target[0][1:] + '//' + raw_target[2]

def files_done(done, time):
    log = 'file_names_parsed-' + time
    with open(log, 'a') as f:
        done = done + '\n'
        f.write(done) 

def create_curl_line(name):
    last_curl_part = '" '
    for key in dict.keys():
        last_curl_part += '-F "' + key + '=' + dict.get(key, '') + '" '
    last_curl_part += url

    return first_curl_part + name + last_curl_part

if __name__ == '__main__':
    script_path = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
    url = 'http://www.linklion.org:8080/portal/rest/file/upload'
    dict = {'existing-source-uri': '',
        'new-source-name': 's-name',
        'new-source-urispace': 's-uri',
        'existing-target-uri': '',
        'new-target-name': 't-name',
        'new-target-urispace': 't-uri',
        'existing-framework-uri': '',
        'new-framework-name': 'LIMES',
        'new-framework-version': '0.5',
        'new-framework-url': 'http://limes.aksw.org',
        'existing-algorithm-uri': '',
        'new-algorithm-name': 'limesDefault',
        'new-algorithm-url': 'http://link.springer.com/article/10.1007%2Fs13740-012-0012-y'} 

    first_curl_part = 'curl -i -F "file=@' + script_path + '/'
    date_time = time.strftime("%Y-%m-%d-%X")

    for name in glob.glob('*/positive.nt'):
        print 'working on file ', name, ' ...'
        set_st_uris(name)
        cmd_line = create_curl_line(name)

        #print cmd_line
        args = shlex.split(cmd_line)
        proc = subprocess.Popen(args, stdout=subprocess.PIPE)
        (out, err) = proc.communicate()
        print out
        files_done(name, date_time)
