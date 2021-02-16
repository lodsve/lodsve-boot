#!/bin/bash
#
# Copyright © 2020 Sun.Hao(https://www.crazy-coder.cn/)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program. If not, see <http://www.gnu.org/licenses/>.
#


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
