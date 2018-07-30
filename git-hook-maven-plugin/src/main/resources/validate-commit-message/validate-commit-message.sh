#!/bin/bash
# Just for commit-msg hook
# hook.version@1.0.1

COLOR_RED='\033[0;31m'
COLOR_NONE='\033[0m'

# 提交日志类型
COMMIT_TYPE_ARRAR=("feat" "fix" "docs" "style" "refactor" "test" "chore")

# 帮助文档
refer_commit_guide='https://docs.google.com/document/d/1QrDFcIiPjSLDn3EL15IJygNPiHORgU1_OOAqWjiDU5Y/edit#'

commit_msg=$(cat "$1" |grep -v "^[#,;]")
commit_line_num=$(echo "$commit_msg" | wc -l) # grep -v "^$"
commit_msg_head=$(echo "$commit_msg" | head -1)

# ----------------------
# Description: 提交日志不能为空
# Author: arthinking
# ----------------------
checkBlank() {
  if [ ${commit_line_num} -eq 0 ]
  then
    echo -e "${COLOR_RED}\nCommit log error: Commit message can not be empty." >&2
    echo -e "${COLOR_NONE} - Refer commit guide: ${refer_commit_guide}\n" >&2
    exit 1
  fi
}

# ----------------------
# Description: 提交日志 header 需要按照正确的格式
# Author: arthinking
# ----------------------
checkHeaderFormat() {
  local header_regex='.*\(.*\): .*'
  local count=$(echo "${commit_msg_head}" | grep -Ec "$header_regex")
  if [ ${count} -eq 0 ]
  then
    echo -e "${COLOR_RED}\nCommit log error: First commit message line (commit header) does not follow format: type(scope): subject" >&2
    echo -e "${COLOR_NONE} - Refer commit guide: ${refer_commit_guide}\n" >&2
    exit 1
  fi

}

# ----------------------
# Description: 提交日志 header 中的 type只能是指定的类型
# Author: arthinking
# ----------------------
checkHeaderType() {
  OLD_IFS="$IFS"
  local type_str=$( IFS=$'|'; echo "${COMMIT_TYPE_ARRAR[*]}" )
  IFS=$OLD_IFS
  local header_regex='('${type_str}')\(.*\): .*'
  local count=$(echo "${commit_msg_head}" | grep -Ec "$header_regex")
  if [ ${count} -eq 0 ]
  then
    echo -e "${COLOR_RED}\nCommit log error: Commit type illegal, expected: ${COMMIT_TYPE_ARRAR[@]}" >&2
    echo -e "${COLOR_NONE} - Refer commit guide: ${refer_commit_guide}\n" >&2
    exit 1
  fi
}

# ----------------------
# Description: 第二行必须为空行
# Author: arthinking
# ----------------------
checkSecondLine() {
  if [ ${commit_line_num} -gt 1 ]
  then
    local second_line_msg=$(echo "$commit_msg" | head -n 2 | tail -n 1 | tr -d " ")
    if [ "${second_line_msg}" != "" ]
    then
      echo -e "${COLOR_RED}\nCommit log error: Commit second line must be blank" >&2
      echo -e "${COLOR_NONE} - Refer commit guide: ${refer_commit_guide}\n" >&2
      exit 1
    fi
  fi
}

# ----------------------
# Description: 提交日志中的 body 校验
#   * 第三行必须不能为空
# Author: arthinking
# ----------------------
checkBody() {
  if [ ${commit_line_num} -gt 2 ]
  then
    local third_line_msg=$(echo "$commit_msg" | head -n 3 | tail -n 1 | tr -d " ")
    if [ "${third_line_msg}" = "" ]
    then
      echo -e "${COLOR_RED}\nCommit log error: Commit third line(commit body) could not be blank" >&2
      echo -e "${COLOR_NONE} - Refer commit guide: ${refer_commit_guide}\n" >&2
      exit 1
    fi
  fi
}

checkBlank
checkHeaderFormat
checkHeaderType
checkSecondLine
checkBody
