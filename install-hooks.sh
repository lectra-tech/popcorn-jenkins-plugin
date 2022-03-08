#!/bin/bash

function copyHook () {
  local sourceHook=$1
  local destDir=$2
  local destHook="$destDir/$(basename $sourceHook)"

  if [ ! -f $destHook ]; then
      echo publishing $destHook
      cp -T $sourceHook $destHook
  else
    diff $sourceHook $destHook > /dev/null 2>&1
    if [ $? -ne 0 ]; then
        echo updating $destHook from $sourceHook
        cp -T $sourceHook $destHook
    fi
  fi
}

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
GIT_RELATIVE_HOOKS_DIR=`git config core.hooksPath || echo ".git/hooks"`
GIT_HOOKS_DIR="$SCRIPT_DIR/$GIT_RELATIVE_HOOKS_DIR"

# for HOOK_FILE in $SCRIPT_DIR/hooks/*; do echo $HOOK_FILE; done
for HOOK_FILE in $SCRIPT_DIR/hooks/*; do copyHook "$HOOK_FILE" "$GIT_HOOKS_DIR"; done
