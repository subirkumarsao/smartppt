/**
 * 
 */

$(function (){
	init();
});

function init(){
	checkLink();
}

function checkLink(){
	$.ajax("service/checklink")
	.done(function(result){
		console.log(result);
		if($.trim(result)=="true"){
			window.location="upload.html";
		} else {
			result = null;
			$.wait(2000).then(checkLink);
		}
	});
}

$.wait = function(ms) {
    var defer = $.Deferred();
    setTimeout(function() { defer.resolve(); }, ms);
    return defer;
};