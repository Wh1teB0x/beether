$.getJSON("http://www.bether.sakura.ne.jp/gs/json.txt", function(data){
    for(var i in data){
        alert(data.name);
    }
});


(function() {
  var question = "http://bether.sakura.ne.jp/gs/json.txt";
  $.getJSON( question, {
    tags: "mount rainier",
    tagmode: "any",
    format: "json"
  })
    .done(function( data ) {
      $.each( data.items, function( i, item ) {
        $( "<img>" ).attr( "src", item.media.m ).appendTo( "#images" );
        if ( i === 3 ) {
          return false;
        }
      });
    });
})();
