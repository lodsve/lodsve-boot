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
./tools/logo.sh
case "$1" in
  'versions')
    ./tools/versions.sh $2
	;;
  'deployOss')
    ./tools/deploy.sh release-oss $2
	;;
  'deployGithub')
    ./tools/deploy.sh release-github $2
	;;
  'deployThirdParty')
    ./tools/deploy.sh release-third-party $2
	;;
  *)
    help
esac
