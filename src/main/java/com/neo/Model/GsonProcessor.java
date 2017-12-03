package com.neo.Model;

import java.util.List;

public class GsonProcessor {

    private int code;
    private String message;
    private List<ResultBean> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public class ResultBean {
        /**
         * courseId : 217012
         * courseName : 高等数学（二）
         * schoolShortName : TONGJI
         * termId : 1002321010
         * enrollCount : 62716
         * imgUrl : http://img1.ph.126.net/S_KZHbKd1cVnNakzi7s3nw==/39125021863241218.png
         * schoolName : 同济大学
         */

        private int courseId;
        private String courseName;
        private String schoolShortName;
        private int termId;
        private int enrollCount;
        private String imgUrl;
        private String schoolName;

        public int getCourseId() {
            return courseId;
        }

        public void setCourseId(int courseId) {
            this.courseId = courseId;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getSchoolShortName() {
            return schoolShortName;
        }

        public void setSchoolShortName(String schoolShortName) {
            this.schoolShortName = schoolShortName;
        }

        public int getTermId() {
            return termId;
        }

        public void setTermId(int termId) {
            this.termId = termId;
        }

        public int getEnrollCount() {
            return enrollCount;
        }

        public void setEnrollCount(int enrollCount) {
            this.enrollCount = enrollCount;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }
    }
}
