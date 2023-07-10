#!/usr/bin/env groovy

package ennva.example

class Docker implements Serializable {

    def script

    Docker(script){
        this.script = script;
    }

    def incrementVersion() {
        script.sh("npm version minor")

        def packageJson = script.readJSON file: 'package.json'
        def version = packageJson.version

        script.env.IMAGE_NAME = "${version}-$script.BUILD_NUMBER"
    }

    def runTests() {
        script.sh("npm install")
        script.sh("npm run test")
    }

    def buildDockerImage(String host, String imageName) {
        script.echo "Building docker image"
        script.sh "docker build -t $host/$imageName:${script.env.IMAGE_NAME} ."
    }

    def dockerLogin(String host = '') {
        script.withCredentials([script.usernamePassword(credentialsId: 'nexus-docker-repo', passwordVariable: 'PASS', usernameVariable: 'USERNAME')]){
            script.sh("echo login in $host")
            script.sh("echo $script.PASS | docker login -u $script.USERNAME --password-stdin $host")
        }
    }

    def dockerPush(String host = '', String imageName) {
        script.sh "docker push $host/$imageName:${script.env.IMAGE_NAME}"
    }

    def commitVersionUpdate(String gitRepo = 'gitlab.com/ennvadigit/node-project.git') {
        script.withCredentials([script.usernamePassword(credentialsId: 'gitlab-credential', usernameVariable: 'USER', passwordVariable: 'PWD')]) {
            script.sh('git config --global user.email "admin@gmail.com"')
            script.sh('git config --global user.name "admin"')

            //script.sh("git remote set-url https://${USER}:${PWD}@gitlab.com/ennvadigit/node-project.git")
            script.sh("git remote set-url https://${script.USER}:${script.PWD}@$gitRepo")
            script.sh('git add .')
            script.sh('git commit -m "ci: version updated"')
            script.sh('git push origin HEAD:jenkins-jobs')
        }
    }
}
