#!/usr/bin/env groovy

import java.io.File

def generateDescriptor(fileName, options) {
    def projectVersion
    if (!options.v) {
        // read from build/.version.txt
        def versionFile = new File('build/.version.txt')
        projectVersion = versionFile.exists() ? versionFile.text : ''
        projectVersion = projectVersion.replace('\r', '').replace('\n', '')
    }
    else {
        projectVersion = options.v
    }

    assert projectVersion : 'Version could not be determined, please provide on command line'
    
    def version = projectVersion
    def artifactVersion = projectVersion
    if (version.contains('-SNAPSHOT')) {
        // SNAPSHOT versions can't be uploaded to Bintray, so replace with branch-build#
        assert options.buildNumber : 'SNAPSHOT version, buildNumber required'
        assert options.branch : 'SNAPSHOT version, branch required'
        version = version.replace('-SNAPSHOT', ".b${options.buildNumber}-${options.branch}")
        artifactVersion = artifactVersion.replace('-SNAPSHOT', '.SNAPSHOT')
    }

    def patternVersion = version.replace('.', '\\.')
    def patternArtifactVersion = artifactVersion.replace('.', '\\.')    

    def today = new Date().format('yyyy-MM-dd')
    new File('.artifact-version').text = "${artifactVersion}"
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
        { 
          "includePattern": "build/target/(.*)-(${patternArtifactVersion})-(.*)", 
          "uploadPattern": "/\$1-${patternVersion}-\$3",
          "matrixParams": {
              "override": 1,
              "publish": 1,
              "list_in_downloads": 1
          },
          "list_in_downloads": true
        }
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
    _ longOpt: 'platform', args: 1, 'Platform (e.g. linux.gtk or macosx.macosx), used for finding build artifacts'
    _ longOpt: 'arch', args: 1, 'Architecture (x86 or x86_64)'
    _ longOpt: 'date', args: 1, 'Version release date (e.g. 2019-01-01)'
    _ longOpt: 'filename', args: 1, 'Bintray descriptor file name (default: .bintray.json)'
    _ longOpt: 'uploadPattern', args: 1, 'Upload pattern for Bintray'
    _ longOpt: 'buildNumber', args: 1, 'Build number used for replacing -SNAPSHOT in version'
    _ longOpt: 'branch', args: 1, 'Branch name used for replacing -SNAPSHOT in version'
}

def options = cli.parse(args)

if (options?.p && options?.r && options?.s) {
    def descFileName = options.filename ?: '.bintray.json'
    generateDescriptor(descFileName, options)
    return
}

cli.usage()
