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
sh ./tools/logo.sh
case "$1" in
  'versions')
    sh ./tools/versions.sh $2
	;;
  'deployOss')
    sh ./tools/deploy.sh release-oss $2
	;;
  'deployGithub')
    sh ./tools/deploy.sh release-github $2
	;;
  'deployThirdParty')
    sh ./tools/deploy.sh release-third-party $2
	;;
  *)
    help
esac
