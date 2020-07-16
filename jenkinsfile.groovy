job("Git Pull")
{
 description ("Pulling Code from Github")
scm {
github ('gauravmehta13/Jenkins-Automation-Using-Groovy','master')
}
configure { it / 'triggers' / 'com.cloudbees.jenkins.GitHubPushTrigger' / 'spec' }
steps{
shell('sudo cp * -v /task3 ')
}
}
job("K8s Deployment")
{
description ("Kubernetes deployment")
steps{
shell('''
if sudo /usr/local/bin/kubectl get deployments | grep webtest
then
sudo /usr/local/bin/kubectl rollout restart deployment/webtest
sudo /usr/local/bin/kubectl rollout status deployment/webtest 
else
sudo /usr/local/bin/kubectl apply -f /task3/deployment.yml
sudo /usr/local/bin/kubectl apply -f /task3/pvc.yml
sudo /usr/local/bin/kubectl apply -f /task3/expose.yml
fi
''')
}
 triggers {
        upstream('Git Pull', 'SUCCESS')
    }
}
job("Monitoring")
{
description ("monitoring the website")
steps{
shell(''' status=$(curl -o /dev/null -sw "%{http_code}" http://192.168.99.102:31000)
if [[$status == 200 ]]
then
echo "running"
else
curl -u admin:1234 http://192.168.99.104:8080/job/kub4/build?token=mail
fi ''')
}
triggers {
        upstream('K8s Deployment', 'SUCCESS')
    }
}
job("Mail")
{
description ("Mailing the developer")
 authenticationToken('mail')

 publishers {
        mailer('269mehta@gmail.com', true, true)
    }
triggers {
        upstream('Monitoring', 'SUCCESS')
    }
}
