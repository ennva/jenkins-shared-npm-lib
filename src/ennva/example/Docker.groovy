#!/usr/bin/env groovy

package ennva.example

class Docker implements Serializable {

    def script

    Docker(script){
        this.script = script;
    }

    def incrementVersion() {
        script.sh("npm version minor")

        def packageJson = readJSON file: 'package.json'
        def version = packageJson.version

        env.IMAGE_NAME = "${version}-$BUILD_NUMBER"
    }

    def runTests() {
        script.sh("npm install")
        script.sh("npm run test")
    }

    def buildDockerImage(String host, String imageName) {
        script.echo "Building docker image"
        script.sh "docker build -t $host/$imageName:${script.VERSION} ."
    }

    def dockerLogin(String host = '') {
        script.withCredentials([script.usernamePassword(credentialsId: 'nexus-docker-repo', passwordVariable: 'PASS', usernameVariable: 'USERNAME')]){
            script.sh("echo login in $host")
            script.sh("echo $script.PASS | docker login -u $script.USERNAME --password-stdin $host")
        }
    }

    def dockerPush(String host = '', String imageName) {
        script.sh "docker push $host/$imageName:${script.VERSION}"
    }

    def commitVersionUpdate(String gitRepo = 'gitlab.com/ennvadigit/node-project.git') {
        script.withCredentials([usernamePassword(credentialsId: 'gitlab-credential', usernameVariable: 'USER', passwordVariable: 'PWD')]) {
            script.sh('git config --global user.email "admin@gmail.com"')
            script.sh('git config --global user.name "admin"')

            //script.sh("git remote set-url https://${USER}:${PWD}@gitlab.com/ennvadigit/node-project.git")
            script.sh("git remote set-url https://${USER}:${PWD}@$gitRepo")
            script.sh('git add .')
            script.sh('git commit -m "ci: version updated"')
            script.sh('git push origin HEAD:jenkins-jobs')
        }
    }
}