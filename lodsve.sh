#!/bin/bash

# Help info function
help(){
  echo "--------------------------------------------------------------------------"
  echo ""
  echo "usage: ./lodsve.sh [versions | deployOss | deployGithub | deployThirdParty]"
  echo ""
  echo "versions          Update lodsve-boot versions."
  echo "deployOss         Deploy lodsve-boot to maven repository"
  echo "deployGithub      Deploy lodsve-boot to github repository"
  echo "deployThirdParty  Deploy lodsve-boot to Third-Party repository"
  echo ""
  echo "--------------------------------------------------------------------------"
}


# Start
./.mvn/logo.sh
case "$1" in
  'versions')
    ./.mvn/versions.sh $2
	;;
  'deployOss')
    ./.mvn/deploy.sh release-oss $2
	;;
  'deployGithub')
    ./.mvn/deploy.sh release-github $2
	;;
  'deployThirdParty')
    ./.mvn/deploy.sh release-third-party $2
	;;
  *)
    help
esac
