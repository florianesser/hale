#!/usr/bin/env groovy

import java.io.File

def generateDescriptor(fileName, options) {
    def today = new Date().format('yyyy-MM-dd')
    new File(fileName).text = """
{
    "package": {
        "name": "${options.p}",
        "repo": "${options.r}",
        "subject": "${options.s}"
    },
    
    "version": {
        "name": "${options.v}",
        "released": "$today",
        "gpgSign": false
    },
    
    "files":
    [
        { "includePattern": "build/target/(.*linux.*)", "uploadPattern": "/\$1" }
    ],

    "publish": true
} 
"""
}

def cli = new CliBuilder(usage: 'generate-bintray-descriptor [options]')
cli.with {
    p longOpt: 'package', args: 1, 'Bintray package name [required]'
    r longOpt: 'repo', args: 1, 'Bintray repo [required]'
    s longOpt: 'subject', args: 1, 'Bintray subject (org or user) [required]'
    v longOpt: 'version', args: 1, 'Version name (e.g. 0.5) [required]'
    d longOpt: 'desc', args: 1, 'Version description'
    _ longOpt: 'date', args: 1, 'Version release date (e.g. 2019-01-01)'
    _ longOpt: 'filename', args: 1, 'Bintray descriptor file name (default: .bintray.json)'
}

def options = cli.parse(args)

if (options?.p && options?.r && options?.s && options?.v) {
    def descFileName = options.filename ?: '.bintray.json'
    generateDescriptor(descFileName, options)
    return
}

cli.usage()
