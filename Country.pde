public class Country {

  String name = "";
  int cases = -1;
  int deaths = -1;
  int recoveries = -1;
  float dRate = 0;
  float rRate = 0;
  
  Country(String name_, int cases_, int deaths_, int recoveries_) {
    name = name_;
    cases = cases_;
    deaths = deaths_;
    recoveries = recoveries_;
    if(cases != 0) {
      dRate = float(deaths_) / float(cases_);
      rRate = float(recoveries_) / float(cases_);
    }
  }
}
