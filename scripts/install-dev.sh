#!/bin/bash
# implementation: team3-rdParty pre-commit and pre-push hook installer
# This script is based on a LGPL 3.0 licensed script.
# 
# Original Script Copyright (C) 2023 Original Author
# Modifications Copyright (C) 2024 mirageoasis
#
# This script is modified under the same license, the GNU Lesser General Public License v3.0.

install_git_hooks() {
  local magic_str_commit="team3-rdParty standard pre-commit hook"
  local magic_str_push="team3-rdParty standard pre-push hook"

  # pre-commit hook 설정
  if [ -f .git/hooks/pre-commit ]; then
    grep -Fq "$magic_str_commit" .git/hooks/pre-commit
    if [ $? -eq 0 ]; then
      :
    else
      echo "" >> .git/hooks/pre-commit
      cat scripts/pre-commit.sh >> .git/hooks/pre-commit
    fi
  else
    cp scripts/pre-commit.sh .git/hooks/pre-commit
    chmod +x .git/hooks/pre-commit
  fi

  # pre-push hook 설정
  if [ -f .git/hooks/pre-push ]; then
    grep -Fq "$magic_str_push" .git/hooks/pre-push
    if [ $? -eq 0 ]; then
      :
    else
      echo "" >> .git/hooks/pre-push
      cat scripts/pre-push.sh >> .git/hooks/pre-push
    fi
  else
    cp scripts/pre-push.sh .git/hooks/pre-push
    chmod +x .git/hooks/pre-push
  fi
}

install_git_hooks
