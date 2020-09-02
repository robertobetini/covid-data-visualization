import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Covid_data extends PApplet {

// MADE BY ROBERTO BETINI JUNIOR (aka FRUTOSE)
// Contact: 
// Instagram - @huskyto.siberiano
// Discord - Huskyto#4421
// E-mail - hissi.fantasma@gmail.com
// Special thanks to the Processing foundation for making this work possible, Dan Shiffman for being an awesome person and teacher and The Coding Train community!
// Open data source: https://en.wikipedia.org/wiki/Template:2019%E2%80%9320_coronavirus_pandemic_data

String countries = "United States,Spain,Italy,Germany,United Kingdom,France,Turkey,Iran,China,Russia,Belgium,Brazil,Canada,Netherlands,Switzerland,Portugal,India,Peru,Ireland,Austria,Sweden,Israel,South Korea,Chile,Saudi Arabia,Japan,Ecuador,Poland,Romania,Pakistan,Mexico,Singapore,Denmark,United Arab Emirates,Norway,Czech Republic,Indonesia,Serbia,Australia,Philippines,Belarus,Qatar,Ukraine,Malaysia,Dominican Republic,Panama,Finland,Colombia,Luxembourg,Egypt,South Africa,Morocco,Bangladesh,Argentina,Thailand,Algeria,Moldova,Greece,Kuwait,Hungary,Bahrain,Croatia,Kazakhstan,Iceland,Uzbekistan,Iraq,Estonia,Azerbaijan,Oman,Armenia,Slovenia,Lithuania,Bosnia & Herzegovina,North Macedonia,Puerto Rico,Slovakia,New Zealand,Cuba,Ghana,Afghanistan,Hong Kong,Cameroon,Bulgaria,Tunisia,Ivory Coast,Djibouti,Cyprus,Latvia,Andorra,Lebanon,Costa Rica,Niger,Nigeria,Albania,Burkina Faso,Guinea,Kyrgyzstan,Bolivia,Kosovo,Uruguay,Honduras,San Marino,Palestine,Malta,Jordan,Taiwan,Réunion,Georgia,Senegal,DR Congo,Mauritius,Montenegro,Sri Lanka,Isle of Man,Guatemala,Mayotte,Kenya,Vietnam,Venezuela,Tanzania,Jersey,Guernsey,Mali,El Salvador,Paraguay,Jamaica,Faroe Islands,Somalia,Martinique,Republic of the Congo,Guadeloupe,Rwanda,Brunei,Guam,Gibraltar,Cambodia,Madagascar,Trinidad & Tobago,Ethiopia,Myanmar,Gabon,Northern Cyprus,Liberia,Aruba,French Guiana,Monaco,Sudan,Bermuda,Togo,Liechtenstein,Equatorial Guinea,Barbados,Maldives,Cape Verde,Sint Maarten,Guyana,Zambia,Cayman Islands,Bahamas,French Polynesia,Uganda,Benin,U.S. Virgin Islands,Libya,Guinea-Bissau,Haiti,Macau,Sierra Leone,Eritrea,Mozambique,Syria,Saint Martin,Donetsk PR,Chad,Mongolia,Nepal,Zimbabwe,Angola,Eswatini,Antigua & Barbuda,East Timor,Luhansk PR,Botswana,Laos,Belize,Fiji,New Caledonia,Malawi,Dominica,Namibia,Saint Kitts & Nevis,Saint Lucia,Curaçao,Grenada,Northern Mariana Islands ,Central African Republic,Saint Vincent,Falkland Islands,Greenland,Montserrat,Seychelles,Turks & Caicos Islands,Gambia,Nicaragua,Suriname,Vatican City,Artsakh,Mauritania,Papua New Guinea,Saint Barthélemy,Bhutan,Burundi,Somaliland,British Virgin Islands,São Tomé & Príncipe,South Sudan,Abkhazia,Anguilla,Saba,Sint Eustatius,Bonaire,Guantanamo Bay,Saint Pierre & Miquelon,Yemen";
String[] countriesArr = split(countries, ",");
Country[] data = new Country[countriesArr.length];
int n = 0; // number of countries that failed geting the data
int total_cases = 0;
int total_deaths = 0;
int total_recoveries = 0;
float avg_dRate = 0, avg_rRate = 0;
float[] std_dev;
float mX = 0, tempX = 0;
boolean on = false;
int y_base;

public void loadData() {
  String url = "https://en.wikipedia.org/wiki/Template:2019%E2%80%9320_coronavirus_pandemic_data";
  String[] lines = loadStrings(url);
  String html = join(lines, "");
  int cases;
  int deaths;
  int recoveries;
  for(int i = 0; i < countriesArr.length; i++) {
    String name = countriesArr[i];
    if(countriesArr[i].contains("&")) {
      int ind = name.indexOf("&");
      name += " ";
      name = name.substring(ind + 1, name.length() - 1);
    }
    // we know that the country's name is right before an </a>, and the data is shortly after the country's name
    String txt = textBetween(html, name + "</a>", "200");
    txt = textBetween(txt, "<td>", "50");
    cases = giveMeNumbers(textBetween(txt, "0", "</td>"));
    // we're slicing the string to cut off the number of cases
    txt = textBetween(txt, "<td>", "50");
    deaths = giveMeNumbers(textBetween(txt, "0", "</td>"));
    // we're slicing the string to cut off the number of deaths, ramining recoveries only
    txt = textBetween(txt, "<td>", "50");
    recoveries = giveMeNumbers(textBetween(txt, "0", "100"));
    if(name.lastIndexOf(" ") == name.length() - 1) {
      name = name.substring(0, name.length() - 1);
      data[i] = new Country(name, cases, deaths, recoveries);
    } else {
      if(cases == -1 || deaths == -1 || recoveries == -1) {
        n++;
        data[i] = new Country("FAILED", cases, deaths, recoveries);
        print(countriesArr[i] + " FAILED.");
      } else {
        data[i] = new Country(countriesArr[i], cases, deaths, recoveries); 
        total_cases += cases;
        total_deaths += deaths;
        total_recoveries += recoveries;
        avg_dRate += data[i].dRate;
        avg_rRate += data[i].rRate;
        print(countriesArr[i] + " done!");
      }
    }
  }
  avg_dRate /= data.length;
  avg_rRate /= data.length;
  std_dev = stdDev(data);
}

public String textBetween(String s, String start, String end) {
  
  // receives a String s, and finds the substring between two other strings (start and end)
  // if start of end is a 'number string', then it rather finds the substring between two indexes
  
  String found = "";
  int startI;
  if(start == "0") {
    startI = 0;
  } else if(PApplet.parseInt(start) > 0) {
    if(PApplet.parseInt(start) > s.length() - 2) {
      startI = s.length() - 2;
    } else {
      startI = PApplet.parseInt(start);
    }
  } else {
    startI = s.indexOf(start) + start.length();
  }
  int endI;
  if(PApplet.parseInt(end) > 0){
    if(startI + PApplet.parseInt(end) < s.length()) {
      endI = startI + PApplet.parseInt(end);
    } else {
      endI = s.length() - 1;
    }
  } else {
    endI = s.indexOf(end);
  }
  if((startI >= 0 && endI >= 0) && startI < endI) {
    found = s.substring(startI, endI);
  } else {
    print("Nothing found.");
  }
  return found;
}

public int giveMeNumbers(String s) {
  
  // if there are any number characters in a string s, then it returns the int number, excluding all other characters
  if(s == "") {
    return -1;
  }
  char[] chars = s.toCharArray();
  String num = "";
  String numbers = "0123456789";
  for(int i = 0; i < chars.length; i++) {
    Character ch = new Character(chars[i]);
    if(numbers.contains(ch.toString())) {
      num += ch.toString();
    }
  }
  return PApplet.parseInt(num);
}

public float[] stdDev(Country[] c) {
  
  // returns an array {std dev for death rate, std dev for recovery rate}
  
  float[] std = new float[2];
  for(int i = 0; i < c.length; i++) {
    if(c[i].name != "FAILED") {
      std[0] += sq(avg_dRate - c[i].dRate);
      std[1] += sq(avg_rRate - c[i].rRate);
    }
  }
  std[0] /= c.length - 1;
  std[0] = sqrt(std[0]);
  std[1] /= c.length - 1;
  std[1] = sqrt(std[1]);
  return std;
}

public void setup() {
  //size(800, 600);
  
  y_base = height - 100;
  loadData();
  for(int i = 0; i < data.length; i++) {
    //print(data[i].dRate + "    ");
  }
  textAlign(RIGHT);
}

public void draw() {
  background(255);
  int maxP = height - 200;
  float w = 0.025f * width, off = width / 10;
  
  // making the effect that you can drag the mouse to scroll the screen
  if(on) {
    mX = tempX - mouseX;
    if(mX > 15040) {
      mX = 15040; 
    } else if(mX > 0) {
      mX = 0;
    }
  }
  push();
  
  // drawing the bars
  translate(mX, y_base);
  noStroke();
  for(int i = 0; i < data.length; i++) {
    if(data[i].name != "FAILED") {
      float h_death = map(data[i].dRate, 0, 1, 0, maxP);
      float h_rec = map(data[i].rRate, 0, 1, 0, maxP);
      fill(180, 0, 0);
      rect(2*off + 2*i*w + i*off, -h_death, w, h_death);
      fill(0, 180, 0);
      rect(2*off + w + 2*i*w + i*off, -h_rec, w, h_rec);
    }
  }
  
  // drawing the countries' names
  push();
  fill(0);
  textAlign(CENTER);
  for(int i = 0; i < data.length; i++) {
    if(data[i].name == "FAILED") {
      text("Failed loading", 2.25f*off + 2*i*w + i*off, 20);
    } else {
      text(data[i].name, 2.25f*off + 2*i*w + i*off, 20);
      text(data[i].cases + " cases", 2.25f*off + 2*i*w + i*off, 40);
    }
  }
  pop();
  
  pop();
  
  // drawing the x and y axis
  line(off, 0, off, height);
  for(int i = 0; i < 10; i++) {
    line(off - 10, y_base - maxP + i*maxP/10, off + 10, y_base - maxP + i*maxP/10);
  }
  for(int i = 0; i < 50; i++) {
    line(off - 3, y_base - maxP + i*maxP/50, off + 3, y_base - maxP + i*maxP/50);
  }
  fill(0);
  for(int i = 0; i < 10; i++) {
    text(str(100 - i*10) + "%", off - 15, y_base - maxP + 4 + i*maxP/10);
  }
  line(0, y_base, 15040, y_base);
  
  // drawing the label
  push();
  textAlign(LEFT);
  textSize(14);
  fill(195);
  rect(1.1f*off, 10, 150, 80);
  fill(180, 0, 0);
  rect(1.2f*off, 20, 30, 20);
  fill(0);
  text("Death rate", 1.2f*off + 35, 35);
  fill(0, 180, 0);
  rect(1.2f*off, 60, 30, 20);
  fill(0);
  text("Recovery rate", 1.2f*off + 35, 75);
  pop();
  
  //drawing the avg and std dev
  push();
  textAlign(LEFT);
  text("AVG world death rate = " + PApplet.parseFloat(round(avg_dRate * 1000)) / 1000, 2.3f*off, 50);
  text("STD DEV world death rate = " + PApplet.parseFloat(round(std_dev[0] * 1000)) / 1000, 2.3f*off + 210, 50);
  text("AVG world recovery rate = " + PApplet.parseFloat(round(avg_rRate * 1000)) / 1000, 2.3f*off, 70);
  text("STD DEV world recovery rate = " + PApplet.parseFloat(round(std_dev[1] * 1000)) / 1000, 2.3f*off + 210, 70);
  pop();
  push();
  textSize(24);
  textAlign(RIGHT);
  text("Press ESC to close.", width - 30, 60);
  fill(0, 80);
  text("Click and drag your mouse to view more.", width - 30, 90);
  pop();
}

public void mousePressed() {
  tempX = mouseX + mX;
  on = true;
}

public void mouseReleased() {
  on = false;
}
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
      dRate = PApplet.parseFloat(deaths_) / PApplet.parseFloat(cases_);
      rRate = PApplet.parseFloat(recoveries_) / PApplet.parseFloat(cases_);
    }
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "Covid_data" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
