package org.wso2.carbon.connector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wso2.carbon.module.core.SimpleMessageContext;
import org.wso2.carbon.module.core.collectors.CsvCollector;
import org.wso2.carbon.module.csv.CsvColumnSkipper;
import org.wso2.carbon.module.csv.util.ParameterKey;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvColumnSkipperTest {

    @Mock
    private SimpleMessageContext mc;

    @Test
    public void testMediate_correctCsvInputWithValidSkipColumnProperty_correctCsvOutputShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c", "d"});
        csvPayload.add(new String[]{"1", "2", "3", "4"});
        csvPayload.add(new String[]{"2", "3", "4", "5"});

        final String columnsToSkipProperty = "2,4";
        final CsvCollector csvCollector = new CsvCollector(mc, null);

        when(mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP)).thenReturn(columnsToSkipProperty);
        when(mc.getCsvArrayStream()).thenReturn(csvPayload.stream());
        when(mc.getCsvPayload()).thenReturn(csvPayload);
        when(mc.collectToCsv()).thenReturn(csvCollector);

        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvColumnSkipper csvColumnSkipper = new CsvColumnSkipper();
        csvColumnSkipper.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expected = "a,c\n" +
                "1,3\n" +
                "2,4\n";

        Assertions.assertEquals(expected, setPayload);

    }

    @Test
    public void testMediate_largeCsvInput_correctCsvOutputShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"id", "first_name", "last_name", "email", "gender", "ip_address"});
        csvPayload.add(new String[]{"1", "Darrel", "Knottley", "dknottley0@columbia.edu", "Male", "174.158.144.88"});
        csvPayload.add(new String[]{"2", "Mortie", "Poulsum", "mpoulsum1@tiny.cc", "Male", "78.150.58.169"});
        csvPayload.add(new String[]{"3", "Anallese", "Vlies", "avlies2@pcworld.com", "Female", "249.151.60.49"});
        csvPayload.add(new String[]{"4", "Iolande", "Cartlidge", "icartlidge3@weebly.com", "Female", "178.15.113.25"});
        csvPayload.add(new String[]{"5", "Oralia", "Jellings", "ojellings4@slate.com", "Female", "207.140.30.36"});
        csvPayload
                .add(new String[]{"6", "Brandie", "Franscioni", "bfranscioni5@tinyurl.com", "Female", "66.45.171.212"});
        csvPayload.add(new String[]{"7", "Tully", "Rait", "trait6@sbwire.com", "Male", "15.104.255.21"});
        csvPayload.add(new String[]{"8", "Vonny", "Klima", "vklima7@unicef.org", "Female", "183.92.133.212"});
        csvPayload.add(new String[]{"9", "Jude", "Deas", "jdeas8@imdb.com", "Male", "27.147.193.193"});
        csvPayload.add(new String[]{"10", "Melisenda", "Buntin", "mbuntin9@europa.eu", "Female", "204.141.62.197"});
        csvPayload.add(new String[]{"11", "Sascha", "Ferrier", "sferriera@dailymail.co.uk", "Male", "160.164.92.104"});
        csvPayload.add(new String[]{"12", "Jesse", "Thrush", "jthrushb@nasa.gov", "Female", "61.250.218.244"});
        csvPayload.add(new String[]{"13", "Vivyanne", "Everil", "veverilc@upenn.edu", "Female", "186.30.158.203"});
        csvPayload.add(new String[]{"14", "Blanche", "Sammars", "bsammarsd@dailymail.co.uk", "Female", "221.32.25.83"});
        csvPayload.add(new String[]{"15", "Sauveur", "Cisland", "scislande@jalbum.net", "Male", "241.32.37.90"});
        csvPayload.add(new String[]{"16", "Brittan", "Holliar", "bholliarf@webnode.com", "Female", "87.10.43.160"});
        csvPayload.add(new String[]{"17", "Fancy", "Perigoe", "fperigoeg@chron.com", "Female", "26.137.221.66"});
        csvPayload.add(new String[]{"18", "Griffin", "de Villier", "gdevillierh@hp.com", "Male", "77.126.4.156"});
        csvPayload.add(new String[]{"19", "Marchall", "Challicombe", "mchallicombei@bloglovin.com", "Male",
                "108.239.197.132"});
        csvPayload.add(new String[]{"20", "Jennine", "Torresi", "jtorresij@bravesites.com", "Female", "94.74.37.3"});
        csvPayload.add(new String[]{"21", "Lucius", "Pineaux", "lpineauxk@constantcontact.com", "Male",
                "57.253.115.215"});
        csvPayload.add(new String[]{"22", "Drusie", "Kerbey", "dkerbeyl@berkeley.edu", "Female", "176.116.247.131"});
        csvPayload.add(new String[]{"23", "Kevyn", "Ollin", "kollinm@sogou.com", "Female", "65.214.252.109"});
        csvPayload.add(new String[]{"24", "Lynett", "Demangeot", "ldemangeotn@chicagotribune.com", "Female",
                "213.7.67.67"});
        csvPayload.add(new String[]{"25", "Calv", "Fitzgerald", "cfitzgeraldo@mapquest.com", "Male", "67.117.196.14"});
        csvPayload.add(new String[]{"26", "Manon", "Webland", "mweblandp@usda.gov", "Female", "76.2.175.132"});
        csvPayload.add(new String[]{"27", "Denice", "Oty", "dotyq@dyndns.org", "Female", "87.56.196.176"});
        csvPayload.add(new String[]{"28", "Roseann", "Yuryev", "ryuryevr@jimdo.com", "Female", "80.171.207.144"});
        csvPayload.add(new String[]{"29", "Dion", "Lamas", "dlamass@yolasite.com", "Female", "134.251.157.158"});
        csvPayload.add(new String[]{"30", "Demetre", "Crew", "dcrewt@booking.com", "Male", "126.51.102.212"});
        csvPayload.add(new String[]{"31", "Alysia", "O'Shirine", "aoshirineu@joomla.org", "Female", "163.221.141.111"});
        csvPayload.add(new String[]{"32", "Elwin", "Able", "eablev@comsenz.com", "Male", "172.189.243.72"});
        csvPayload.add(new String[]{"33", "Flo", "Rudolfer", "frudolferw@usa.gov", "Female", "44.221.211.171"});
        csvPayload.add(new String[]{"34", "Noelani", "Eslie", "nesliex@illinois.edu", "Female", "12.72.186.53"});
        csvPayload.add(new String[]{"35", "Tedmund", "Tsar", "ttsary@ifeng.com", "Male", "169.183.177.139"});
        csvPayload.add(new String[]{"36", "Ninetta", "Baybutt", "nbaybuttz@ibm.com", "Female", "167.227.152.115"});
        csvPayload.add(new String[]{"37", "Sigismund", "Fulker", "sfulker10@t-online.de", "Male", "215.71.44.101"});
        csvPayload.add(new String[]{"38", "Ardis", "Urling", "aurling11@usda.gov", "Female", "58.77.174.40"});
        csvPayload.add(new String[]{"39", "Glenine", "Harrowsmith", "gharrowsmith12@twitpic.com", "Female",
                "118.84.167.96"});
        csvPayload.add(new String[]{"40", "Valencia", "Earry", "vearry13@angelfire.com", "Female", "51.239.61.55"});
        csvPayload.add(new String[]{"41", "Cosette", "Renard", "crenard14@msu.edu", "Female", "234.74.6.78"});
        csvPayload.add(new String[]{"42", "Alon", "Djekic", "adjekic15@163.com", "Male", "93.3.146.41"});
        csvPayload.add(new String[]{"43", "Atalanta", "Fricke", "africke16@over-blog.com", "Female", "17.53.253.150"});
        csvPayload.add(new String[]{"44", "Caren", "Urpeth", "curpeth17@indiegogo.com", "Female", "15.16.148.148"});
        csvPayload.add(new String[]{"45", "Madella", "Kennedy", "mkennedy18@skype.com", "Female", "196.42.25.182"});
        csvPayload.add(new String[]{"46", "Sonnie", "Baines", "sbaines19@hc360.com", "Male", "202.49.155.93"});
        csvPayload.add(new String[]{"47", "Kendricks", "Lisimore", "klisimore1a@nsw.gov.au", "Male", "252.107.205.27"});
        csvPayload.add(new String[]{"48", "Merrel", "Hugonin", "mhugonin1b@uiuc.edu", "Male", "174.145.170.191"});
        csvPayload.add(new String[]{"49", "Mehetabel", "Heisler", "mheisler1c@skype.com", "Female", "133.133.44.212"});
        csvPayload.add(new String[]{"50", "Elston", "Baines", "ebaines1d@meetup.com", "Male", "41.155.226.33"});
        csvPayload.add(new String[]{"51", "Laural", "Lewnden", "llewnden1e@shinystat.com", "Female", "217.41.235.60"});
        csvPayload.add(new String[]{"52", "Penn", "Forsythe", "pforsythe1f@wordpress.com", "Male", "143.93.36.166"});
        csvPayload.add(new String[]{"53", "Isahella", "Giorgio", "igiorgio1g@jalbum.net", "Female", "72.145.59.112"});
        csvPayload.add(new String[]{"54", "Leupold", "Brend", "lbrend1h@bing.com", "Male", "0.229.174.11"});
        csvPayload.add(new String[]{"55", "Adriano", "Barrett", "abarrett1i@fda.gov", "Male", "98.54.87.240"});
        csvPayload.add(new String[]{"56", "Caryn", "Haskey", "chaskey1j@t.co", "Female", "183.123.166.81"});
        csvPayload.add(new String[]{"57", "Leone", "Norcutt", "lnorcutt1k@last.fm", "Female", "42.17.230.33"});
        csvPayload.add(new String[]{"58", "Cherice", "Bayly", "cbayly1l@hibu.com", "Female", "80.100.189.111"});
        csvPayload.add(new String[]{"59", "Nickie", "Koppke", "nkoppke1m@time.com", "Male", "163.77.230.215"});
        csvPayload
                .add(new String[]{"60", "Nathalie", "Travers", "ntravers1n@thetimes.co.uk", "Female", "9.71.144.165"});
        csvPayload.add(new String[]{"61", "Rhonda", "Mandrier", "rmandrier1o@reverbnation.com", "Female",
                "153.175.109.207"});
        csvPayload.add(new String[]{"62", "Derby", "McCerery", "dmccerery1p@un.org", "Male", "233.253.28.178"});
        csvPayload.add(new String[]{"63", "Killie", "Maton", "kmaton1q@google.es", "Male", "10.185.92.94"});
        csvPayload.add(new String[]{"64", "Rasia", "Cope", "rcope1r@bluehost.com", "Female", "226.179.110.160"});
        csvPayload.add(new String[]{"65", "Taffy", "Sturte", "tsturte1s@state.gov", "Female", "51.196.156.15"});
        csvPayload.add(new String[]{"66", "Gabby", "Hagstone", "ghagstone1t@bandcamp.com", "Male", "228.127.124.204"});
        csvPayload.add(new String[]{"67", "Deni", "Vermer", "dvermer1u@google.fr", "Female", "174.213.195.135"});
        csvPayload.add(new String[]{"68", "Sam", "Tarbet", "starbet1v@columbia.edu", "Female", "91.215.111.239"});
        csvPayload.add(new String[]{"69", "Mychal", "Pirot", "mpirot1w@cisco.com", "Male", "114.57.48.188"});
        csvPayload.add(new String[]{"70", "Brooks", "Bompas", "bbompas1x@sfgate.com", "Female", "234.219.93.89"});
        csvPayload.add(new String[]{"71", "Vance", "Basley", "vbasley1y@google.cn", "Male", "211.172.214.202"});
        csvPayload.add(new String[]{"72", "Karry", "Caplan", "kcaplan1z@plala.or.jp", "Female", "239.210.82.199"});
        csvPayload.add(new String[]{"73", "Shay", "Jenkin", "sjenkin20@ed.gov", "Female", "125.40.254.205"});
        csvPayload.add(new String[]{"74", "Clifford", "Halford", "chalford21@clickbank.net", "Male", "248.69.105.213"});
        csvPayload.add(new String[]{"75", "Claudianus", "Colnet", "ccolnet22@cpanel.net", "Male", "7.65.4.238"});
        csvPayload.add(new String[]{"76", "Tanhya", "Kaasmann", "tkaasmann23@buzzfeed.com", "Female", "2.207.53.241"});
        csvPayload.add(new String[]{"77", "Moe", "Flag", "mflag24@washington.edu", "Male", "33.193.24.56"});
        csvPayload.add(new String[]{"78", "Averell", "Bank", "abank25@squidoo.com", "Male", "64.186.186.37"});
        csvPayload.add(new String[]{"79", "Loy", "Trafford", "ltrafford26@nyu.edu", "Male", "57.136.180.124"});
        csvPayload.add(new String[]{"80", "Jori", "Cottell", "jcottell27@cdc.gov", "Female", "201.241.120.147"});
        csvPayload
                .add(new String[]{"81", "Willamina", "Mellmer", "wmellmer28@geocities.jp", "Female", "175.99.132.203"});
        csvPayload.add(new String[]{"82", "Sheffield", "Drysdell", "sdrysdell29@topsy.com", "Male", "91.210.14.208"});
        csvPayload.add(new String[]{"83", "Emlyn", "Skeates", "eskeates2a@a8.net", "Male", "140.213.57.142"});
        csvPayload.add(new String[]{"84", "Brigitta", "Guthrum", "bguthrum2b@usatoday.com", "Female", "191.46.32.80"});
        csvPayload
                .add(new String[]{"85", "Camilla", "Veevers", "cveevers2c@deviantart.com", "Female", "79.139.19.183"});
        csvPayload.add(new String[]{"86", "Hamid", "Grut", "hgrut2d@blogspot.com", "Male", "4.95.52.239"});
        csvPayload.add(new String[]{"87", "Tarra", "Demare", "tdemare2e@theatlantic.com", "Female", "130.116.11.184"});
        csvPayload.add(new String[]{"88", "Audrye", "Cassam", "acassam2f@wikispaces.com", "Female", "206.12.185.138"});
        csvPayload.add(new String[]{"89", "Vera", "Guerrin", "vguerrin2g@dagondesign.com", "Female", "58.2.117.77"});
        csvPayload.add(new String[]{"90", "Greggory", "Leathers", "gleathers2h@about.com", "Male", "179.10.237.233"});
        csvPayload.add(new String[]{"91", "Elie", "Wilkennson", "ewilkennson2i@uol.com.br", "Female", "131.9.2.232"});
        csvPayload.add(new String[]{"92", "Briny", "MacCumeskey", "bmaccumeskey2j@pen.io", "Female", "78.214.226.99"});
        csvPayload.add(new String[]{"93", "Jone", "Gilkison", "jgilkison2k@disqus.com", "Male", "130.101.134.140"});
        csvPayload.add(new String[]{"94", "Winthrop", "Royston", "wroyston2l@adobe.com", "Male", "98.147.217.108"});
        csvPayload.add(new String[]{"95", "Marina", "Fechnie", "mfechnie2m@miibeian.gov.cn", "Female", "40.62.157.86"});
        csvPayload.add(new String[]{"96", "Ewell", "Stocky", "estocky2n@rambler.ru", "Male", "14.195.80.230"});
        csvPayload.add(new String[]{"97", "Carin", "Umpleby", "cumpleby2o@samsung.com", "Female", "229.89.177.73"});
        csvPayload
                .add(new String[]{"98", "Abbe", "Habbijam", "ahabbijam2p@feedburner.com", "Female", "237.147.70.152"});
        csvPayload.add(new String[]{"99", "Isa", "Duesbury", "iduesbury2q@theguardian.com", "Male", "120.223.144.132"});
        csvPayload.add(new String[]{"100", "Neile", "Salla", "nsalla2r@smugmug.com", "Female", "85.210.44.168"});

        final String columnsToSkipProperty = "2,4";
        final CsvCollector csvCollector = new CsvCollector(mc, null);

        when(mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP)).thenReturn(columnsToSkipProperty);
        when(mc.getCsvArrayStream()).thenReturn(csvPayload.stream());
        when(mc.getCsvPayload()).thenReturn(csvPayload);
        when(mc.collectToCsv()).thenReturn(csvCollector);

        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvColumnSkipper csvColumnSkipper = new CsvColumnSkipper();
        csvColumnSkipper.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expected = "id,last_name,gender,ip_address\n" +
                "1,Knottley,Male,174.158.144.88\n" +
                "2,Poulsum,Male,78.150.58.169\n" +
                "3,Vlies,Female,249.151.60.49\n" +
                "4,Cartlidge,Female,178.15.113.25\n" +
                "5,Jellings,Female,207.140.30.36\n" +
                "6,Franscioni,Female,66.45.171.212\n" +
                "7,Rait,Male,15.104.255.21\n" +
                "8,Klima,Female,183.92.133.212\n" +
                "9,Deas,Male,27.147.193.193\n" +
                "10,Buntin,Female,204.141.62.197\n" +
                "11,Ferrier,Male,160.164.92.104\n" +
                "12,Thrush,Female,61.250.218.244\n" +
                "13,Everil,Female,186.30.158.203\n" +
                "14,Sammars,Female,221.32.25.83\n" +
                "15,Cisland,Male,241.32.37.90\n" +
                "16,Holliar,Female,87.10.43.160\n" +
                "17,Perigoe,Female,26.137.221.66\n" +
                "18,de Villier,Male,77.126.4.156\n" +
                "19,Challicombe,Male,108.239.197.132\n" +
                "20,Torresi,Female,94.74.37.3\n" +
                "21,Pineaux,Male,57.253.115.215\n" +
                "22,Kerbey,Female,176.116.247.131\n" +
                "23,Ollin,Female,65.214.252.109\n" +
                "24,Demangeot,Female,213.7.67.67\n" +
                "25,Fitzgerald,Male,67.117.196.14\n" +
                "26,Webland,Female,76.2.175.132\n" +
                "27,Oty,Female,87.56.196.176\n" +
                "28,Yuryev,Female,80.171.207.144\n" +
                "29,Lamas,Female,134.251.157.158\n" +
                "30,Crew,Male,126.51.102.212\n" +
                "31,O'Shirine,Female,163.221.141.111\n" +
                "32,Able,Male,172.189.243.72\n" +
                "33,Rudolfer,Female,44.221.211.171\n" +
                "34,Eslie,Female,12.72.186.53\n" +
                "35,Tsar,Male,169.183.177.139\n" +
                "36,Baybutt,Female,167.227.152.115\n" +
                "37,Fulker,Male,215.71.44.101\n" +
                "38,Urling,Female,58.77.174.40\n" +
                "39,Harrowsmith,Female,118.84.167.96\n" +
                "40,Earry,Female,51.239.61.55\n" +
                "41,Renard,Female,234.74.6.78\n" +
                "42,Djekic,Male,93.3.146.41\n" +
                "43,Fricke,Female,17.53.253.150\n" +
                "44,Urpeth,Female,15.16.148.148\n" +
                "45,Kennedy,Female,196.42.25.182\n" +
                "46,Baines,Male,202.49.155.93\n" +
                "47,Lisimore,Male,252.107.205.27\n" +
                "48,Hugonin,Male,174.145.170.191\n" +
                "49,Heisler,Female,133.133.44.212\n" +
                "50,Baines,Male,41.155.226.33\n" +
                "51,Lewnden,Female,217.41.235.60\n" +
                "52,Forsythe,Male,143.93.36.166\n" +
                "53,Giorgio,Female,72.145.59.112\n" +
                "54,Brend,Male,0.229.174.11\n" +
                "55,Barrett,Male,98.54.87.240\n" +
                "56,Haskey,Female,183.123.166.81\n" +
                "57,Norcutt,Female,42.17.230.33\n" +
                "58,Bayly,Female,80.100.189.111\n" +
                "59,Koppke,Male,163.77.230.215\n" +
                "60,Travers,Female,9.71.144.165\n" +
                "61,Mandrier,Female,153.175.109.207\n" +
                "62,McCerery,Male,233.253.28.178\n" +
                "63,Maton,Male,10.185.92.94\n" +
                "64,Cope,Female,226.179.110.160\n" +
                "65,Sturte,Female,51.196.156.15\n" +
                "66,Hagstone,Male,228.127.124.204\n" +
                "67,Vermer,Female,174.213.195.135\n" +
                "68,Tarbet,Female,91.215.111.239\n" +
                "69,Pirot,Male,114.57.48.188\n" +
                "70,Bompas,Female,234.219.93.89\n" +
                "71,Basley,Male,211.172.214.202\n" +
                "72,Caplan,Female,239.210.82.199\n" +
                "73,Jenkin,Female,125.40.254.205\n" +
                "74,Halford,Male,248.69.105.213\n" +
                "75,Colnet,Male,7.65.4.238\n" +
                "76,Kaasmann,Female,2.207.53.241\n" +
                "77,Flag,Male,33.193.24.56\n" +
                "78,Bank,Male,64.186.186.37\n" +
                "79,Trafford,Male,57.136.180.124\n" +
                "80,Cottell,Female,201.241.120.147\n" +
                "81,Mellmer,Female,175.99.132.203\n" +
                "82,Drysdell,Male,91.210.14.208\n" +
                "83,Skeates,Male,140.213.57.142\n" +
                "84,Guthrum,Female,191.46.32.80\n" +
                "85,Veevers,Female,79.139.19.183\n" +
                "86,Grut,Male,4.95.52.239\n" +
                "87,Demare,Female,130.116.11.184\n" +
                "88,Cassam,Female,206.12.185.138\n" +
                "89,Guerrin,Female,58.2.117.77\n" +
                "90,Leathers,Male,179.10.237.233\n" +
                "91,Wilkennson,Female,131.9.2.232\n" +
                "92,MacCumeskey,Female,78.214.226.99\n" +
                "93,Gilkison,Male,130.101.134.140\n" +
                "94,Royston,Male,98.147.217.108\n" +
                "95,Fechnie,Female,40.62.157.86\n" +
                "96,Stocky,Male,14.195.80.230\n" +
                "97,Umpleby,Female,229.89.177.73\n" +
                "98,Habbijam,Female,237.147.70.152\n" +
                "99,Duesbury,Male,120.223.144.132\n" +
                "100,Salla,Female,85.210.44.168\n";

        Assertions.assertEquals(expected, setPayload);

    }

    @Test
    public void testMediate_correctCsvInputWithValidSkipColumnPropertyNotOrdered_correctCsvOutputShouldSet() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c", "d"});
        csvPayload.add(new String[]{"1", "2", "3", "4"});
        csvPayload.add(new String[]{"2", "3", "4", "5"});

        final String columnsToSkipProperty = "2,4";
        final CsvCollector csvCollector = new CsvCollector(mc, null);

        when(mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP)).thenReturn(columnsToSkipProperty);
        when(mc.getCsvArrayStream()).thenReturn(csvPayload.stream());
        when(mc.getCsvPayload()).thenReturn(csvPayload);
        when(mc.collectToCsv()).thenReturn(csvCollector);

        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvColumnSkipper csvColumnSkipper = new CsvColumnSkipper();
        csvColumnSkipper.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expected = "a,c\n" +
                "1,3\n" +
                "2,4\n";

        Assertions.assertEquals(expected, setPayload);

    }

    @Test
    public void testMediate_outOfIndexProperty_OutOfIndexShouldIgnored() {

        final List<String[]> csvPayload = new ArrayList<>();
        csvPayload.add(new String[]{"a", "b", "c", "d"});
        csvPayload.add(new String[]{"1", "2", "3", "4"});
        csvPayload.add(new String[]{"2", "3", "4", "5"});

        final String columnsToSkipProperty = "2,4,90";
        final CsvCollector csvCollector = new CsvCollector(mc, null);

        when(mc.lookupTemplateParameter(ParameterKey.COLUMNS_TO_SKIP)).thenReturn(columnsToSkipProperty);
        when(mc.getCsvArrayStream()).thenReturn(csvPayload.stream());
        when(mc.getCsvPayload()).thenReturn(csvPayload);
        when(mc.collectToCsv()).thenReturn(csvCollector);

        ArgumentCaptor<String> payloadSetArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CsvColumnSkipper csvColumnSkipper = new CsvColumnSkipper();
        csvColumnSkipper.mediate(mc);

        verify(mc).setTextPayload(payloadSetArgumentCaptor.capture());
        String setPayload = payloadSetArgumentCaptor.getValue();

        final String expected = "a,c\n" +
                "1,3\n" +
                "2,4\n";

        Assertions.assertEquals(expected, setPayload);

    }
}