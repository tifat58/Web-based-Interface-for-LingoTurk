(function () {
    var app = angular.module('_TEMPLATE_ExperimentApp', ["Lingoturk"]);

    app.controller('RenderController', ['$http', '$timeout', '$scope', function ($http, $timeout, $scope) {
        var self = this;
        self.state = "";
        self.allStates = [];
        self.questions = [];
        self.part = null;
        self.slideIndex = 0;
        self.questionIndex = 0;
        self.expId = null;
        self.questionId = null;
        self.partId = null;
        self.origin = null;
        self.hitId = "";
        self.assignmentId = "";
        self.workerId = "";
        self.subListMap = {};
        self.subListsIds = [];
        self.showMessage = "none";
        self.redirectUrl = null;

        self.shuffleQuestions = true;
        self.shuffleSublists = true;
        self.useGoodByeMessage = true;
        self.useStatistics = false;

        self.statistics = [
            {name : "Age", type: "number", answer : undefined},
            {name : "Gender", type: "text", answer : ""},
            {name : "Nationality", type: "text", answer : ""},
            {name : "Mother's first language", type: "text", answer : ""},
            {name : "Father's first language", type: "text", answer : ""},
            {name : "Are you bilingual (grown up with more than one language)?", type: "boolean", answer : undefined},
            {name : "Please list the languages you speak at at the advance level.", type: "text", answer : "", optional : true}
        ];

        self.subslide = 1; // Variable used to switch slides

        // Function to switch slides. Adding it into default file.
        this.switchSlide = function(){
            self.subslide = self.subslide + 1;
            this.load(); //TODO: CONFIRM THIS IS OK!
        };

        // Function to check max word length. Adding this to the default file.
        this.checkMaxWordCount = function(answer, maxWordCount) {
            return answer.split(" ").length > Number(maxWordCount);
        };

        // Function to check min word length. Adding this to the default file.
        this.checkMinWordCount = function(answer, minWordCount) {
            return answer.split(" ").length < Number(minWordCount);
        };

        this.preventCharacters = function(answer, chars) {
            var regex = new RegExp("["+String(chars)+"]");
            console.log("["+String(chars)+"]");
            console.log(answer);
            console.log(regex.test(answer));
            // if initially undefined answer, then no need to check any chars.
            if (answer === undefined) {
                return false;
            };
            return regex.test(answer);
        };

        this.resultsSubmitted = function(){
            self.subListsIds.splice(0,1);
            if(self.subListsIds.length > 0 ){
                self.showMessage = "nextSubList";
            }else{
                self.processFinish();
            }
        };

        this.processFinish = function(){
            if(!self.useGoodByeMessage){
                self.finished();
            }else{
                self.showMessage = "goodBye";
            }
        };

        this.finished = function(){
            if(self.origin == null || self.origin == "NOT AVAILABLE"){
                bootbox.alert("Results successfully submitted. You might consider redirecting your participants now.");
            }else if(self.origin == "MTURK"){
                $("#form").submit();
            }else if(self.origin == "PROLIFIC"){
				if(inIframe()){
                    window.top.location.href = self.redirectUrl;
                }else{
                    window.location = self.redirectUrl;
                }
            }
        };

        this.nextSublist = function(){
            self.questionIndex = 0;
            self.questions = self.subListMap[self.subListsIds[0]];
            self.showMessage = "none";
        };

        this.resultSubmissionError = function(){
            self.failedTries = 0;
            bootbox.alert("An error occurred while submitting your results. Please try again in a few seconds.");
        };

        this.handleError = function(){
            if(self.failedTries < 100){
                ++self.failedTries;
                setTimeout(function() { self.submitResults(self.resultsSubmitted, self.handleError) }, 1000);
            }else{
                self.resultSubmissionError();
            }
        };

        self.failedTries = 0;
        this.submitResults = function (successCallback, errorCallback) {
            var results = {
                experimentType : "_TEMPLATE_Experiment",
                results : self.questions,
                expId : self.expId,
                origin : self.origin,
                statistics : self.statistics,
                assignmentId : self.assignmentId,
                hitId : self.hitId,
                workerId : self.workerId,
                partId : (self.partId == null ? -1 : self.partId)
            };


            $http.post("/submitResults", results)
                .success(successCallback)
                .error(errorCallback);
        };

        this.next = function(){
            if(self.state == "workerIdSlide"){
                if(self.questionId == null && self.partId == null){
                    self.load(function(){
                        self.state = self.allStates[++self.slideIndex];
                    });
                    return;
                }
            }

            if(self.slideIndex + 1 < self.allStates.length){
                self.state = self.allStates[++self.slideIndex];
            }else{
                self.submitResults(self.resultsSubmitted, self.handleError);
            }
        };

        this.nextQuestion = function(){
            if(self.questionIndex + 1 < self.questions.length){
                ++self.questionIndex;
            }else{
                self.next();
            }
        };

        this.load = function(callback){
            var subListMap = self.subListMap;

            if(self.questionId != null){
                $http.get("/getQuestion/" + self.questionId).success(function (data) {
                    self.questions = [data];

                    subListMap[self.questions[0].subList] = [self.questions[0]];

                    if(callback !== undefined){
                        callback();
                    }
                });
            }else if(self.partId != null){
                $http.get("/returnPart?partId=" + self.partId).success(function (data) {
                    var json = data;
                    self.part = json;
                    self.questions = json.questions;

                    if(self.shuffleQuestions){
                        shuffleArray(self.part.questions);
                    }

                    for(var i = 0; i < self.questions.length; ++i){
                        var q = self.questions[i];
                        if (subListMap.hasOwnProperty(q.subList)){
                            subListMap[q.subList].push(q);
                        }else{
                            subListMap[q.subList] = [q];
                            self.subListsIds.push(q.subList);
                        }
                    }
                    if(self.shuffleSublists){
                        shuffleArray(self.subListsIds);
                    }
                    self.questions = self.subListMap[self.subListsIds[0]];

                    if(callback !== undefined){
                        callback();
                    }
                });
            }else{
                $http.get("/getPart?expId=" + self.expId + "&workerId=" + self.workerId).success(function (data) {
                    var json = data;
                    self.part = json;
                    self.partId = json.id;
                    self.questions = json.questions;

                    if(self.shuffleQuestions){
                        shuffleArray(self.part.questions);
                    }

                    for(var i = 0; i < self.questions.length; ++i){
                        var q = self.questions[i];
                        if (subListMap.hasOwnProperty(q.subList)){
                            subListMap[q.subList].push(q);
                        }else{
                            subListMap[q.subList] = [q];
                            self.subListsIds.push(q.subList);
                        }
                    }
                    if(self.shuffleSublists){
                        shuffleArray(self.subListsIds);
                    }
                    self.questions = self.subListMap[self.subListsIds[0]];

                    if(callback !== undefined){
                        callback();
                    }
                });
            }
        };

        $(document).ready(function () {
            self.questionId = ($("#questionId").length > 0) ? $("#questionId").val() : null;
            self.partId = ($("#partId").length > 0) ? $("#partId").val() : null;
            self.expId = ($("#expId").length > 0) ? $("#expId").val() : null;
            self.hitId = ($("#hitId").length > 0) ? $("#hitId").val() : "NOT AVAILABLE";
            self.workerId = ($("#workerId").length > 0) ? $("#workerId").val() : "";
            self.assignmentId = ($("#assignmentId").length > 0) ? $("#assignmentId").val() : "NOT AVAILABLE";
            self.origin = ($("#origin").length > 0) ? $("#origin").val() : "NOT AVAILABLE";
            self.redirectUrl = ($("#redirectUrl").length > 0) ? $("#redirectUrl").val() : null;

            if(self.questionId != null || self.partId != null){
                self.load();
            }

            self.allStates = ["instructionsSlide","workerIdSlide","statisticsSlide","questionSlide"];

            if(!self.useStatistics){
                var index = self.allStates.indexOf("statisticsSlide");
                self.allStates.splice(index,1);
            }

            if(self.workerId.trim() != ""){
                var index = self.allStates.indexOf("workerIdSlide");
                self.allStates.splice(index,1);
            }

            $scope.$apply(self.state = self.allStates[0]);

            $(document).on("keypress", ":input:not(textarea)", function(event) {
                if (event.keyCode == 13) {
                    event.preventDefault();
                }
            });
        });
    }]);
})();


