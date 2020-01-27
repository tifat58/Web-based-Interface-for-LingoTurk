/* This file is created and compiled by Ayan Majumder, Shakhwat Ahmed Nobin, Hasan Md Tusfiqur Alam, Rayhanul Islam Rumel, Akshay Akshay,
* Chirag Bhuvaneshwara and Khaushik Chowdhury as a Software Engineering Course project in winter semester 2019/20 */
(function () {
    var app = angular.module('EditExperiment', ["Lingoturk"]);

    app.controller('EditController', ['$http', '$timeout', '$scope', function ($http, $timeout, $scope) {
        $scope.window = window;
        $scope.alert = alert;
        $scope.stringify = JSON.stringify;
        $scope.radioOptions = '';
        var self = this;
        this.id = -1;
        this.types = [];
        this.type = "";
        this.name = "";
        this.nameOnAmt = "";
        this.description = "";
        this.additionalExplanations = "";
        this.questionType = null;
        this.groupType = null;
        this.section = []
        this.exampleQuestions = [];
        this.groups = [];
        this.files = [];
        this.validForm = false;
        //this is the backbone structure. Everything will be inside this.slides
        this.slides = [{
            'num': 1,
            'name': 'Slide 1',
            'components': []
        }];

        //dropdown values for useScale field of SlideAnswer Component
        self.useScaleOptions = [
            {name: "nlgadequacy"},
            {name: "likelihood"},
            {name: "scale"},
            {name: "custom"}
        ];

        //dropdown values for options of checkBox Component.
        self.radioCheckBoxOptions = [
            {name: "options"},
            {name: "descriptions"}
        ];

        //dropdown values for options of radio Component
        self.radioOptions = [
            {name: "options"}
        ];

        //dropdown values for options of dragDrop Component
        self.dragDropOptions = [
            {name: "connectives"}
        ];

        //dropdown values for options of sliderAnswer Component
        self.sliderAnswerOptions = [
            {name: "options"}
        ];

        //dropdown values for conditions of radio and checkbox Component
        self.trueFalse = [
            {name: "true"},
            {name: "false"}
        ];
        //dropdown values for middleBottom options of dragDrop Component
        self.middleBottom = [
            {name: "middle"},
            {name: "bottom"}
        ];
        //dropdown values for displayStyle options of dragDrop Component
        self.displayStyle = [
            {name: 'default'},
            {name: 'comment'},
            {name: 'chat'}
        ];
        //dropdown values for noneStyle options of dragDrop Component
        self.noneStyleOptions = [
            {name: 'inline'},
            {name: 'block'}
        ];

        // dropdown values for encoding options of fileInput Component
        self.encodingOptions = [
            {name : 'UTF-8'},
            {name : 'UTF-16'}
        ];

        // ADD NEW COMPONENT CUSTOM DROPDOWN VALUE HERE

        //defining some variable
        $scope.fieldValues = {};
        $scope.fieldType ={};
        $scope.componentName = '';
        this.questionFieldNames = [];
        this.optionFields = [];
        this.conditionFields = [];
        this.delimiter = ',';
        this.commentSequence = '#';
        this.questionFields = [];
        self.questionColumnNames = null;
        $scope.map = '';

        $scope.range = function (max) {
            var arr = [];
            for (var i = 0; i < max; ++i) {
                arr.push(i);
            }
            return arr;
        };
        $scope.slideNum = 1;
        var run = false;

        //done editing button click action. if ok then submit slides data to backend and redirect to home page
        this.submit = function(){
            var experiment = {
                name: self.type,
                slides: this.slides

            };
            //loop through slide and check if there is any empty component or not.
            angular.forEach(this.slides, function(value, key){
                //if an empty component then show the msg and will not allow to submit
                if(value.components.length === 0){
                    run = false;

                }else{
                    run = true;
                }
            });
            // call the submitEditExperiment route along with all the data.
            if(run) {
                $http.post("/submitEditExperiment", experiment)
                    .success(function () {
                        bootbox.alert("Experiment Edited. You will be redirected to the index page!", function () {
                            window.location.href = "/";
                        });
                    })
                    .error(function (e) {
                        alert("Error!" + e);
                    });
            }else {
                bootbox.alert("There is at least one slide with empty component. Please delete empty slides or add component there.");
            }
        };
        // function for change the component position 1 spot up
        this.compPosUp = function (slide,field) {
            var slideIndex = this.slides.indexOf(slide);
            var index = this.slides[slideIndex].components.indexOf(field);
            var temp;
            if(index > 0){
                temp = this.slides[slideIndex].components[index - 1];
                this.slides[slideIndex].components[index - 1] = this.slides[slideIndex].components[index];
                this.slides[slideIndex].components[index] = temp
            }
        };
        // function for change the component position 1 spot down
        this.compPosDown = function (slide,field) {
            var slideIndex = this.slides.indexOf(slide);
            var index = this.slides[slideIndex].components.indexOf(field);
            var temp;
            if(index !== this.slides[slideIndex].components.length){
                temp = this.slides[slideIndex].components[index + 1];
                this.slides[slideIndex].components[index + 1] = this.slides[slideIndex].components[index];
                this.slides[slideIndex].components[index] = temp
            }
        };
        // function for change the slide position 1 spot up
        this.slidePosUp = function (slide) {
            var slideIndex = this.slides.indexOf(slide);
            var temp;
            var previous;
            var last;
            if(slideIndex > 0){
                temp = this.slides[slideIndex - 1];
                previous = parseInt(temp.num);
                last = parseInt(this.slides[slideIndex].num);
                this.slides[slideIndex - 1] = this.slides[slideIndex];
                this.slides[slideIndex - 1].num = previous;
                this.slides[slideIndex] = temp;
                this.slides[slideIndex].num = last;
            }
        };
        //store the component name into global variable and calling the modal to map
        this.addElement = function(value){
            $scope.componentName = value;
            this.componentName = value;
            if(value === 'dragDrop'){
                //loading the default values for drag drop
                $scope.fieldType.randomizeConnectives = 'true';
                $scope.fieldType.splitter = ',';
                $scope.fieldType.content = '';
                $scope.fieldType.htmlInput = 'false';
                $scope.fieldType.boxPosition = 'middle';
                $scope.fieldType.boxTitle = 'Candidate connectives';
                $scope.fieldType.displayStyle = 'default';
                $scope.fieldType.allowMultiple = 'false';
                $scope.fieldType.instructions1 = 'Please drag the best-suited connective into the green target box below.';
                $scope.fieldType.instructions2 = 'You can now drag one more connective into the box below.';
                $scope.fieldType.goBackText = 'go back';
                $scope.fieldType.allowNoneOfThese = 'true';
                $scope.fieldType.noneOfTheseText = 'none of these';
                $scope.fieldType.allowNone = 'false';
                $scope.fieldType.allowNoneText = 'no connective';
                $scope.fieldType.noneStyle = 'block';
                $scope.fieldType.defaultConnective = '';
                $scope.fieldType.contentClass = 'col-md-12';
                $scope.fieldType.click = 'RC.nextQuestion';
            }
            //initially create two mandatory options field for checkbox and radio
            if(value === 'radio' || value === 'checkBox'){
                for (var i = 0; i < 2; ++i) {
                    this.addOptionField()
                }
            }
            if(this.slides.length){
                $('#myModal').modal('show');
            }else {
                bootbox.alert('There is no Slide right now. Please add a slide first.');
            }
        };
        //component data structure and creating component object
        this.Element = function (field,option,conditions) {
            var self = this
            var options = '';
            var descriptions = '';
            var connectives = '';
            var i = 0;
            var j = 1;
            var k = 0;
            var connectives_counter = 0;
            self.name = $scope.componentName;
            self.model_attr =  field.type;
            self.options = [];
            self.conditions = [];
            //manually inserting the mandatory option fields of drag and drop
            if($scope.componentName === "dragDrop"){

                self.options[0] = {
                    "name": 'connectives',
                    "value": field.connectives
                };
                self.options[1] = {
                    "name": 'context1',
                    "value": field.context1
                };
                self.options[2] = {
                    "name": 'context2',
                    "value": field.context2
                };
                self.options[3] = {
                    "name": 'sentence1',
                    "value": field.sentence1
                };
                self.options[4] = {
                    "name": 'sentence2',
                    "value": field.sentence2
                };
                self.options[5] = {
                    "name": 'content',
                    "value": field.content
                };
                self.options[6] = {
                    "name": 'htmlInput',
                    "value": field.htmlInput
                };
                self.options[7] = {
                    "name": 'boxPosition',
                    "value": field.boxPosition
                };
                self.options[8] = {
                    "name": 'boxTitle',
                    "value": field.boxTitle
                };
                self.options[9] = {
                    "name": 'displayStyle',
                    "value": field.displayStyle
                };
                self.options[10] = {
                    "name": 'allowMultiple',
                    "value": field.allowMultiple
                };
                self.options[11] = {
                    "name": 'instructions1',
                    "value": field.instructions1
                };
                self.options[12] = {
                    "name": 'instructions2',
                    "value": field.instructions2
                };
                self.options[13] = {
                    "name": 'goBackText',
                    "value": field.goBackText
                };
                self.options[14] = {
                    "name": 'allowNoneOfThese',
                    "value": field.allowNoneOfThese
                };
                self.options[15] = {
                    "name": 'noneOfTheseText',
                    "value": field.noneOfTheseText
                };
                self.options[16] = {
                    "name": 'allowNone',
                    "value": field.allowNone
                };
                self.options[17] = {
                    "name": 'allowNoneText',
                    "value": field.allowNoneText
                };
                self.options[18] = {
                    "name": 'noneStyle',
                    "value": field.noneStyle
                };
                self.options[19] = {
                    "name": 'defaultConnective',
                    "value": field.defaultConnective
                };
                self.options[20] = {
                    "name": 'contentClass',
                    "value": field.contentClass
                };
                self.options[21] = {
                    "name": 'click',
                    "value": field.click
                };
                self.options[22] = {
                    "name": 'randomizeConnectives',
                    "value": field.randomizeConnectives
                };
                self.options[23] = {
                    "name": 'splitter',
                    "value": field.splitter
                };

                j = 24;
            }
            //condition fields for textAnswer
            if($scope.componentName === "textAnswer" || $scope.componentName === "textArea"){
                self.conditions[0] = {
                    "name": 'minWordCount',
                    "value": field.minWordCount
                };
                self.conditions[1] = {
                    "name": 'maxWordCount',
                    "value": field.maxWordCount
                };
                self.conditions[2] = {
                    "name": 'preventCharacters',
                    "value": field.preventCharacters
                };
            }

            //Encoding option for fileinput
            if($scope.componentName === "fileInput"){

                self.options[0] = {
                    "name": 'encoding',
                    "value": field.encoding
                }
            }

            //ADD NEW COMPONENT OPTIONS AND CONDITIONS HERE

            //conditions for checkBox,radio and starAnswer
            if($scope.componentName === "checkBox" || $scope.componentName === "radio" || $scope.componentName === "starAnswer"){
                self.conditions[0] = {
                    "name": 'mandatory',
                    "value": field.mandatory
                };

                if($scope.componentName === "checkBox") {
                    self.conditions[1] = {
                        "name": 'allowMultipleSelection',
                        "value": field.allowMultipleSelection
                    };
                }

                // Start: added for maxStars
                if($scope.componentName === "starAnswer") {
                    self.options[0] = {
                        "name": 'maxStars',
                        "value": field.maxStars
                    }
                }
                // end: added for maxStars

            }

            //inserting options and descriptions and connectives concat in comma separately
            angular.forEach(option, function(value, key){

                if(value.name === 'options'){
                    if(i === 0){
                        options =  value.optionType;
                        i++;
                    }
                    else {
                        options = options + ',' + value.optionType;
                    }
                }
                else if(value.name === 'descriptions'){
                    if(k === 0){
                        descriptions = value.optionType;
                        k++;
                    }
                    else {
                        descriptions = descriptions + ',' + value.optionType;
                    }
                }
                else if(value.name === 'connectives'){
                    if(connectives_counter === 0){
                        connectives = field.connectives + ',' + value.optionType;
                        connectives_counter++;
                    }
                    else {
                        connectives = connectives + ',' + value.optionType;
                    }
                }
                else {
                    //if not options and descriptions and connectives then insert
                    self.options[j] = {
                        "name": value.name,
                        "value": value.optionType
                    };
                    j++;
                }
                //self.slides[self.slides.length - 1].components[testRowIndex -1].options.push(new self.Option(value))
            });
            //inserting condition into data structure
            angular.forEach(conditions, function(value, key){
                    self.conditions[key] = {
                        "name": value.name,
                        "value": value.optionType
                    };
            });
            //for drag drop connectivites always push the concat value to 0 position of options array
            if(connectives_counter > 0){
                self.options[0] = {
                    "name": 'connectives',
                    "value": connectives
                }
            }
            //for options of checkbox,radio always push the concat value to 0 position of options array
            if(i > 0){
                self.options[0] = {
                    "name": 'options',
                    "value": options
                }
            }
            //for descriptions of checkbox always push the concat value to 0 position of options array
            if(k > 0){
                self.options[1] = {
                    "name": 'descriptions',
                    "value": descriptions
                }
            }

            if($scope.componentName === "sliderAnswer") {
                //for sliderAnswer component we pushed useScale at 0 position and options at position 1. This is different then checkbox and radio
                //here we are checking if there is any options then re-allocate the option field to position 1 and useScale at position 0
                if(self.options[0]){
                    var temp = self.options[0];
                    self.options[0] = {
                        "name": 'useScale',
                        "value": field.useScale
                    };
                    self.options[1] = temp;
                }else{
                    self.options[0] = {
                        "name": 'useScale',
                        "value": field.useScale
                    }
                }
            }
            $scope.componentName = '';
            $scope.fieldType = {};
        };

        //creating slide object
        this.Slide = function (index) {
            var self = this;
            self.num = index;
            self.name = 'New Slide';
            self.components = [];
        };

        //creating option object
        this.Option = function (item) {
            var self = this;
            self.name = 'options';
            self.value = item.optionType;
        };

        //creating new component
        this.addNewElement = function (fieldType,slideNum) {
            if(fieldType.ques) {
                fieldType.type = fieldType.ques
            }
            //pushing the created object to slide
            this.slides[slideNum - 1].components.push(new self.Element(fieldType,this.optionFields,this.conditionFields));
            this.optionFields = [];
            this.conditionFields = [];
            $scope.fieldType.type = '';
            $scope.fieldType.ques = '';
            $scope.slideNum = this.slides.length
            $("#myModal").modal('hide')

        };
        //edit component values. Need to load again to the modal with the previously inserted data.
        //so we will parse the data structure here and put the value into scope to show them on edit modal
        this.editElement = function(slide,field){
            //parsing all the value and put them into $scope variable to show them into html
            var slideIndex = this.slides.indexOf(slide);
            var index = this.slides[slideIndex].components.indexOf(field);
            $scope.fieldValues.componentName = this.slides[slideIndex].components[index].name;
            $scope.fields = this.slides[slideIndex].components[index].model_attr;
            $scope.fieldValues.model_attr = this.slides[slideIndex].components[index].model_attr;
            $scope.fieldValues.slideIndex = slideIndex;
            $scope.fieldValues.componentIndex = index;
            this.optionFields = $scope.fieldValues.type = this.slides[slideIndex].components[index].options;
            //at edit modal need to show the concat options and connectives in different fields
            var counter = 0;
            var temp = self.optionFields;
            self.optionFields = [];
            //parsing the options for the component. as some data is already in concat form so need to break them and make field out of them
            angular.forEach(temp, function(value, key){
                if(value.name === 'options' || value.name === 'connectives' || value.name === 'descriptions'){
                    //split and store in array
                    var array = value.value.split(',');
                    //loop through array and replace the first one and push the others
                    var current = 0;
                    if(value.name === 'connectives'){
                        //as per our design there are 24 options in dragDrop and manually added new connectives are at position 0 and again start from 24
                        for (var i= 0; i<array.length; i++){
                            if (current === 0){
                                self.optionFields.push({'name': value.name, 'value': array[i]});
                                current = 24;
                                counter ++;
                            }else {
                                //pushing the value after 24 as there are already 24 options in dragDrop
                                self.optionFields[current] = {'name': value.name, 'value': array[i]};
                                current++;
                            }
                        }
                    }
                    else {
                        // pushing values for options and descriptions
                        for (var i= 0; i<array.length; i++){
                            self.optionFields.push({'name': value.name, 'value': array[i]});
                            counter ++;
                        }
                    }
                }
                //for all components where there is no options,descriptions or connectives
                else {
                    if($scope.fieldValues.componentName === 'dragDrop'){
                        self.optionFields[key] = {'name': value.name, 'value': value.value};
                    }else {
                        self.optionFields.push({'name': value.name, 'value': value.value});
                        counter ++;
                    }
                }
            })
            //push all the conditions into conditionFields variable for showing in html
            this.conditionFields = this.slides[slideIndex].components[index].conditions;
            $('#editModal').modal('show');

        };
        //edit a component from a slide. I guess i have missed something here. need to add condition here
        this.editAddedElement = function(slide){
            var options = '';
            var descriptions = '';
            var name = '';
            var current = 0;
            var has_description = false;
            var has_options = false;
            var has_connectives = false;
            var current_des= 0;
            //here we want to again concat the options, connectives, descriptions into comma separate into string
            for (var i=0; i < this.optionFields.length; i++){
                name = this.optionFields[i].name;
                if(name === 'options' || name === 'connectives'){
                    //check if there is any options or not
                    if (name === 'options'){
                        has_options = true;
                    }
                    //check if there is any connectives or not
                    if (name === 'connectives') {
                        has_connectives = true;
                    }
                    // here we use this current variable to add comma after the first element. concat into one variable
                    if(current === 0){
                        options = this.optionFields[i].value;
                        current ++;
                    }else {
                        options = options + ',' + this.optionFields[i].value;
                    }
                }
                // also concat into one variable for description
                if (name === 'descriptions'){
                    has_description = true;
                    if(current_des === 0){
                        descriptions = this.optionFields[i].value;
                        current_des++;
                    }else {
                        descriptions = descriptions + ',' + this.optionFields[i].value;
                    }
                }
            }
            // now to save the new values along with comma separate value first remove those index where we have options,connectives or descriptions
            for (var i=0; i < this.optionFields.length; i++){
                if(this.optionFields[i].name === 'options' ||  this.optionFields[i].name === 'descriptions'){
                    //if there is any options or descriptions which is after position 23 then remove those and decrement the counter i as the array size is reduced
                    this.optionFields.splice(i,1);
                    i--;
                }
                if(this.optionFields[i].name === 'connectives' && i > 23){
                    //if there is any connectives which is after position 23 then remove those and decrement the counter i as the array size is reduced
                    this.optionFields.splice(i,1);
                    i--;
                }
            }
            // if has options then at position 0 push comma separated concat options for all and position 1 for slideAnswer Component
            if(has_options){
                if($scope.fieldValues.componentName === 'sliderAnswer'){
                    this.optionFields[1] = {'name': 'options', 'value': options};
                }else{
                    this.optionFields[0] = {'name': 'options', 'value': options};
                }
            }
            // if has connectives then at position 0 push comma separated concat connectives
            if(has_connectives){
                this.optionFields[0] = {'name': 'connectives', 'value': options};
            }
            // at position 1 push  comma separated concat descriptions
            if (has_description){
                this.optionFields[1] = {'name': 'descriptions', 'value': descriptions};
            }
            //inserting optionsFields and ConditionFields
            this.slides[$scope.fieldValues.slideIndex].components[$scope.fieldValues.componentIndex].model_attr = $scope.fieldValues.model_attr;
            this.slides[$scope.fieldValues.slideIndex].components[$scope.fieldValues.componentIndex].options = this.optionFields;
            this.slides[$scope.fieldValues.slideIndex].components[$scope.fieldValues.componentIndex].conditions = this.conditionFields;
            // clearing the globally assigned variables
            self.conditionFields = [];
            self.fieldValues = [];
            self.optionFields = [];
            self.optionFields = [];
            //close the modal
            $("#myModal").modal('hide')

        };

        //delete a component from a slide
        this.removeField = function (slide,field) {
            //fine the index which are going to be deleted
            var slideIndex = this.slides.indexOf(slide);
            var index = this.slides[slideIndex].components.indexOf(field);
            this.slides[slideIndex].components.splice(index, 1);
            bootbox.alert("You have successfully deleted component " + field.name);
        };

        // adding a new slide
        this.addSlide = function(){
            var index = this.slides.push(new self.Slide(this.slides.length + 1));
            $scope.slideNum = this.slides.length;
        };

        //deleting a slide
        this.removeSlide = function(slide) {
            var index = this.slides.indexOf(slide);
            this.slides.splice(index, 1);
            $scope.slideNum = this.slides.length;
            bootbox.alert("You have successfully deleted slide " + (index + 1));

        };

        //data object for options
        this.Field = function () {
            var self = this;
            self.name = "";
            self.type = "";
        };
        //this function check if the max value is less then the min value or not. If so then set max value equal to min value
        this.checkAmount = function (e,minValue,value) {
            console.log(minValue,value)
            if(value < minValue){
                $scope.fieldType.maxWordCount = minValue;
                e.preventDefault();
            }
        };
        //for edit modal set the max value
        this.setMaxValue = function (value) {
            $('#maxWordCount').val(value);
        };
        //for edit modal check if the max value is smaller then min value or not
        this.checkMax = function (value) {
            var min = $('#minWordCount').val();
            var max = $('#maxWordCount').val();
            if(max < min){
                $('#maxWordCount').val(min);
            }
        };

        //add new options
        this.addOptionField = function () {
            self.optionFields.push(new self.Field());
        };

        //add new condition
        this.addConditionField = function () {
            self.conditionFields.push(new self.Field());
        }

        //delete options function
        this.removeOptionField = function (field) {
            var index = self.optionFields.indexOf(field);
            self.optionFields.splice(index, 1);
        };

        //delete options function
        this.removeConditionField = function (field) {
            var index = self.conditionFields.indexOf(field);
            self.conditionFields.splice(index, 1);
        };

        //refreshing value when closing the modal
        this.modalClose = function(){
            this.optionFields = [];
            this.conditionFields = [];
            $scope.fields = [];
            $scope.fieldValues = [];
        };

        //open modal for slide change name and set values to variables
        this.openChangeSlideModal = function(slide) {
            var slideIndex = this.slides.indexOf(slide);
            $scope.fieldValues.slideIndex = slideIndex;
            $scope.fieldValues.slideName = this.slides[slideIndex].name;
            $('#slideNameChangeModal').modal('show');
        };

        // for sliderAnswer onchange method where we are checking if the value is not custom then remove all options
        this.checkUseScale = function (value,edit) {
            if(value != 'custom'){
                if (edit){
                    var temp = self.optionFields[0];
                    self.optionFields = [];
                    self.optionFields[0] = temp;
                }else {
                    self.optionFields = [];
                }
            }
        };
        //this function change the name of the slide
        this.changeSlideName = function(data){
            this.slides[data.slideIndex].name = data.slideName;
            $scope.fieldValues = [];
        }

        //getting fields name from backend
        $(document).ready(function () {
            if (typeof String.prototype.startsWith != 'function') {
                String.prototype.startsWith = function (str) {
                    return this.slice(0, str.length) == str;
                };
            }

            self.type = $("#experimentName").val().trim();

            $http.get("/getExperimentDetails?experimentName=" + self.type).success(function (data) {

                self.types = data;
                for (var typeName in data) {
                    if (!data.hasOwnProperty(typeName)) continue;
                    var obj = data[typeName];
                    var fields = obj.fields;

                    if (obj.isQuestionType) {
                        obj.name = typeName;
                        self.questionType = obj;
                    }

                    if (obj.isGroupType) {
                        obj.name = typeName;
                        self.groupType = obj;
                    }

                    var tmp = [];
                    if(!(fields instanceof Array)){
                        for (var key in fields) {
                            if (fields.hasOwnProperty(key)) {
                                tmp.push(key);
                            }
                        }
                    }else{
                        for (var i = 0; i < fields.length; ++i) {
                            var f = fields[i];
                            tmp.push(f.name);
                        }
                    }

                    var func = "self[typeName] = function(" + tmp.join(",") + "){\nvar self = this;\nself._type=\"" + typeName + "\";\n";
                    if(obj.isGroupType){
                        func + "self.questions=null;\n";
                    }
                    for (var i = 0; i < fields.length; ++i) {
                        var f = fields[i];
                        func += "self." + f.name + " = " + f.name + ";\n"
                    }
                    func += "};";
                    eval(func);
                }

                self.questionFieldNames = self.questionType.fields;
                //this.questionFieldNames = self.questionFieldNames;
                self.usedNames = [];

            });
        })
    }
    ]);
    //this function split the camelcase word into separate words
    app.filter('splitCamelCase', [function () {
        return function (input) {
            if (typeof input !== "string") {
                return input;
            }
            return input.split(/(?=[A-Z])/).join(' ');
        };
    }]);
})();
