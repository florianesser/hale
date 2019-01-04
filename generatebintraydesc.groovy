#!/usr/bin/env groovy

import java.io.File

def generateDescriptor(fileName, options) {
    def version
    if (!options.v) {
        // read from build/.version.txt
        def versionFile = new File('build/.version.txt')
        version = versionFile.exists() ? versionFile.text : ''
        version = version.replace('\r', '').replace('\n', '')
    }
    else {
        version = options.v
    }
    
    if (version.contains('-SNAPSHOT')) {
        // SNAPSHOT versions can't be uploaded to Bintray, so replace with branch-build#
        assert options.buildNumber : 'SNAPSHOT version, buildNumber required'
        assert options.branch : 'SNAPSHOT version, branch required'
        version = version.replace('-SNAPSHOT', ".${options.buildNumber}-${options.branch}")
    }

    assert version : 'Version could not be determined, please provide on command line'
    
    def today = new Date().format('yyyy-MM-dd')
    new File(fileName).text = """
{
    "package": {
        "name": "${options.p}",
        "repo": "${options.r}",
        "subject": "${options.s}"
    },
    
    "version": {
        "name": "${version}",
        "released": "$today",
        "gpgSign": false
    },
    
    "files":
    [
        { "includePattern": "build/target/(.*${options.a}.*)", "uploadPattern": "/\$1" }
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
    v longOpt: 'version', args: 1, 'Version name (e.g. 0.5), read from build/.version.txt if omitted'
    d longOpt: 'desc', args: 1, 'Version description'
    a longOpt: 'arch', args: 1, 'Architecture (e.g. linux or macosx), used for finding build artifacts'
    _ longOpt: 'date', args: 1, 'Version release date (e.g. 2019-01-01)'
    _ longOpt: 'filename', args: 1, 'Bintray descriptor file name (default: .bintray.json)'
    _ longOpt: 'uploadPattern', args: 1, 'Upload pattern for Bintray'
    _ longOpt: 'buildNumber', args: 1, 'Build number used for replacing -SNAPSHOT in version'
    _ longOpt: 'branch', args: 1, 'Branch name used for replacing -SNAPSHOT in version'
}

def options = cli.parse(args)

if (options?.p && options?.r && options?.s && options?.a) {
    def descFileName = options.filename ?: '.bintray.json'
    generateDescriptor(descFileName, options)
    return
}

cli.usage()
