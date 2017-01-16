#!/usr/bin/env python

# The following curl statement is created for every *.nt file in a 
# directory (example):
# curl -i 
# -F "file=@/home/markus/Mapping/limesSpecsAll/bnb-dbpedia-novelists/positive.nt" 
# -F "source-name=BNB" 
# -F "source-uri=http://bnb.data.bl.uk" 
# -F "target-name=DBpedia"
# -F "target-uri=http://dbpedia.org" 
# -F "new-framework-name=LIMES" 
# -F "new-framework-version=1.2" 
# -F "new-framework-url=http://limes.aksw.org" 
# -F "new-algorithm-name=EAGLE" 
# -F "new-algorithm-version=3.0" 
# -F "new-algorithm-url=http://www.foo.com"
# http://www.linklion.org/portal/rest/file/upload
#
# Usage:
# This script grabs all files named (example) aei.pitt.edu---cogprints.org.nt 
# taking first part of name as source and second part of name as target name -
# if your file format differs, you need to change the importer.
#
# Some information is static, change it here.
#
# If you process large amounts of files, test it first with 1 or 2 files.

import inspect, os, glob, shlex, subprocess, time

def set_st_uris(name):
    with open(name) as f:
        fl = f.readline()
    parts = name.partition('---')
    source = parts[0]
    target = parts[2][:-3]
    
    pre_source = fl.partition(source)[0][1:]
    pre_target = fl.rsplit(target, 1)[0].rsplit('> <', 1)[1]
    
    dict['new-source-name'] = source
    dict['new-source-urispace'] = pre_source + source
    dict['new-target-name'] = target
    dict['new-target-urispace'] = pre_target + target

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
        'new-framework-name': 'sameasOrgDumpFramework',
        'new-framework-version': '2012-07-21',
        'new-framework-url': 'http://datahub.io/de/dataset/sameas-org',
        'existing-algorithm-uri': '',
        'new-algorithm-name': 'sameasOrgDumpAlgorithm',
        'new-algorithm-url': 'http://www.opendefinition.org/licenses/cc-zero'} 

    first_curl_part = 'curl -i -F "file=@' + script_path + '/'
    date_time = time.strftime("%Y-%m-%d-%X")

    for name in glob.glob('*.nt'):
        print 'working on file ', name, ' ...'
        set_st_uris(name)
        cmd_line = create_curl_line(name)

        #print cmd_line
        args = shlex.split(cmd_line)
        proc = subprocess.Popen(args, stdout=subprocess.PIPE)
        (out, err) = proc.communicate()
        print out
        files_done(name, date_time)
